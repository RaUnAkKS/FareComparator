package com.transportoptimizer.Services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class DistanceService {

    /**
     * Calculates a deterministic pseudo-distance (1.0 km to 25.0 km)
     * based on hash of origin & destination.
     */
    public double calculateDistanceKm(String origin, String destination) {
        int seed = Math.abs(Objects.hash(origin, destination)) % 2500;
        double distanceKm = 1 + (seed / 100.0);

        log.debug("Calculated pseudo-distance between '{}' and '{}' = {} km",
                origin, destination, distanceKm);

        return distanceKm;
    }

    /**
     * Async version using CompletableFuture.
     */
    public CompletableFuture<Double> calculateDistanceKmAsync(String origin, String destination) {
        return CompletableFuture.supplyAsync(() -> calculateDistanceKm(origin, destination));
    }
}
