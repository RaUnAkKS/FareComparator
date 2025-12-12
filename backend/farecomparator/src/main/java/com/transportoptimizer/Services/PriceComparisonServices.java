package com.transportoptimizer.Services;


import com.transportoptimizer.entity.FareEstimate;
import com.transportoptimizer.entity.ProviderFare;
import com.transportoptimizer.entity.TripRequest;
import com.transportoptimizer.provider.ProviderClient;
import com.transportoptimizer.Repository.FareEstimateCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriceComparisonServices{

    private final DistanceService distanceService;
    private final PriceNormalizationServices normalizationService;
    private final FareEstimateCacheRepository cacheRepository;
    private final List<ProviderClient> providerClients;

    public FareEstimate compareFares(TripRequest tripRequest) {
        long start = System.currentTimeMillis();

        String origin = tripRequest.getOrigin();
        String destination = tripRequest.getDestination();

        // 1) Compute distance
        double distanceKm = distanceService.calculateDistanceKm(origin, destination);

        log.info("Distance between '{}' and '{}' computed as {} km",
                origin, destination, distanceKm);

        // 2) Query all providers in parallel
        List<ProviderFare> rawFares = providerClients
                .parallelStream()
                .map(provider -> {
                    try {
                        ProviderFare fare = provider.getFare(origin, destination);
                        log.debug("Provider {} returned raw fare {}", provider.providerId(), fare);
                        return fare;
                    } catch (Exception e) {
                        log.error("Error from provider {}: {}", provider.providerId(), e.getMessage());
                        return null; // skip this provider safely
                    }
                })
                .filter(f -> f != null)
                .collect(Collectors.toList());

        // 3) Normalize fares
        List<ProviderFare> normalizedFares =
                normalizationService.normalizeAll(rawFares, distanceKm);

        // 4) Sort fares by price
        List<ProviderFare> sortedFares = normalizedFares.stream()
                .sorted(Comparator.comparingDouble(ProviderFare::getPrice))
                .collect(Collectors.toList());

        // 5) Build FareEstimate
        FareEstimate estimate = FareEstimate.builder()
                .estimateId("est-" + System.currentTimeMillis())
                .origin(origin)
                .destination(destination)
                .totalDistanceKm(distanceKm)
                .providerFares(sortedFares)
                .timestamp(Instant.now())
                .build();

        // 6) Save to cache if distance < 100 km
        if (distanceKm < 100) {
            try {
                cacheRepository.save(estimate);
                log.info("FareEstimate cached for {} -> {}", origin, destination);
            } catch (Exception e) {
                log.error("Failed to cache FareEstimate: {}", e.getMessage());
            }
        }

        long end = System.currentTimeMillis();
        log.info("Fare comparison completed in {} ms for userId={}",
                (end - start), tripRequest.getUserId());

        return estimate;
    }

    public CompletableFuture<FareEstimate> compareFaresAsync(TripRequest tripRequest) {
        return CompletableFuture.supplyAsync(() -> compareFares(tripRequest));
    }
}
