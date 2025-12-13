package com.transportoptimizer.Controller;

import com.transportoptimizer.Services.PriceComparisonServices;
import com.transportoptimizer.Services.RecommendationService;
import com.transportoptimizer.Repository.FareHistoryRepository;

import com.transportoptimizer.dto.CompareRequestDTO;
import com.transportoptimizer.dto.CompareResponseDTO;
import com.transportoptimizer.dto.ProviderFareDTO;

import com.transportoptimizer.entity.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/api/v1/compare")
@RequiredArgsConstructor
@Slf4j
public class CompareController {

    private final PriceComparisonServices fareComparisonService;
    private final RecommendationService recommendationService;
    private final FareHistoryRepository fareHistoryRepository;

    @PostMapping
    public ResponseEntity<CompareResponseDTO> compare(@Valid @RequestBody CompareRequestDTO dto) {
        try {
            long start = System.currentTimeMillis();

            // STEP 1: Convert DTO → TripRequest
            TripRequest trip = TripRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .origin(dto.getOrigin())
                    .destination(dto.getDestination())
                    .userId(dto.getUserId())
                    .preferCheapest(dto.isPreferCheapest())
                    .preferFastest(dto.isPreferFastest())
                    .departureTime(dto.getDepartureTime())
                    .build();

            // STEP 2: Fetch all provider fares
            FareEstimate estimate = fareComparisonService.compareFares(trip);

            // STEP 3: Generate recommendation
            Suggestion suggestion = recommendationService.recommendBestMode(estimate, trip);

            // STEP 4: Build response DTO for frontend
            CompareResponseDTO.RecommendationMeta recommendationMeta =
                    CompareResponseDTO.RecommendationMeta.builder()
                            .chosenProviderId(suggestion.getChosenProviderId())
                            .confidenceScore(suggestion.getConfidenceScore())
                            .reason(suggestion.getReason())
                            .chosenFare(
                                    new ProviderFareDTO(
                                            suggestion.getChosenFare().getProviderId(),
                                            suggestion.getChosenFare().getProviderName(),
                                            suggestion.getChosenFare().getPrice(),
                                            suggestion.getChosenFare().getEtaMinutes(),
                                            suggestion.getChosenFare().isSurge(),
                                            suggestion.getChosenFare().getMetadata(),
                                            suggestion.getChosenFare().getVehicleType()
                                    )
                            )
                            .build();

            CompareResponseDTO.ResponseMeta meta =
                    CompareResponseDTO.ResponseMeta.builder()
                            .timestamp(Instant.now())
                            .serviceVersion("v1.0")
                            .traceId("TRACE-" + System.currentTimeMillis())
                            .build();

            CompareResponseDTO response = CompareResponseDTO.builder()
                    .requestId(trip.getRequestId())
                    .origin(estimate.getOrigin())
                    .destination(estimate.getDestination())
                    .totalDistanceKm(estimate.getTotalDistanceKm())
                    .sortedFares(
                            estimate.getProviderFares().stream()
                                    .map(fare -> new ProviderFareDTO(
                                            fare.getProviderId(),
                                            fare.getProviderName(),
                                            fare.getPrice(),
                                            fare.getEtaMinutes(),
                                            fare.isSurge(),
                                            fare.getMetadata(),
                                            fare.getVehicleType()
                                    ))
                                    .collect(Collectors.toList())
                    )
                    .recommendation(recommendationMeta)
                    .meta(meta)
                    .build();
            double maxPrice = estimate.getProviderFares()
                    .stream()
                    .mapToDouble(ProviderFare::getPrice)
                    .max()
                    .orElse(0);

            double minPrice = estimate.getProviderFares()
                    .stream()
                    .mapToDouble(ProviderFare::getPrice)
                    .min()
                    .orElse(0);

            double savings = maxPrice - minPrice;


            // STEP 5: Save History in MongoDB (EXACT STRUCTURE AS YOUR MODEL)
            FareHistory history = FareHistory.builder()
                    .historyId(null)
                    .userId(dto.getUserId())
                    .tripRequest(trip)
                    .fareEstimate(estimate)
                    .chosenProviderId(suggestion.getChosenProviderId())
                    .chosenProviderName(
                            suggestion.getChosenFare() != null
                                    ? suggestion.getChosenFare().getProviderName()
                                    : suggestion.getChosenProviderId()
                    )
                    .chosenFare(suggestion.getChosenFare())
                    .savings(savings)
                    .createdAt(Instant.now())
                    .build();

            fareHistoryRepository.save(history);

            log.info("compare() completed in {} ms", (System.currentTimeMillis() - start));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Comparison failed: {}", e.getMessage(), e);
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Comparison failed");
        }
    }
}
