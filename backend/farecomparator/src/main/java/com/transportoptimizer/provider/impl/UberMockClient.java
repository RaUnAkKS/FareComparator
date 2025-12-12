package com.transportoptimizer.provider.impl;

import com.transportoptimizer.entity.ProviderFare;
import com.transportoptimizer.provider.ProviderClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UberMockClient implements ProviderClient {

    @Override
    public String providerId() {
        return "uber-mock";
    }

    @Override
    public String providerName() {
        return "Uber (Mock)";
    }

    @Override
    public ProviderFare getFare(String origin, String destination) {
        return getFaresBatch(origin, destination, null).get(0);
    }

    @Override
    public List<ProviderFare> getFaresBatch(String origin, String destination, Map<String, Object> options) {
        double distance = pseudoDistance(origin, destination);

        return List.of(
                buildFare("Uber Go", "cab", distance, 12, 6, 12),
                buildFare("Uber Premier", "premium_cab", distance, 18, 6, 12),
                buildFare("Uber Auto", "auto", distance, 8, 4, 8),
                buildFare("Uber Moto", "bike", distance, 6, 3, 6)
        );
    }

    private ProviderFare buildFare(String vehicleName, String vehicleType, double distance, double rate,
                                   int minEta, int maxEta) {

        double base = distance * rate;
        double surgeFactor = random(0.9, 1.5);
        boolean surge = surgeFactor > 1.25;

        return ProviderFare.builder()
                .providerId(providerId())
                .providerName(vehicleName)
                .vehicleType(vehicleType)
                .distanceKm(distance)
                .price(base * surgeFactor)
                .etaMinutes((int) random(minEta, maxEta))
                .currency("INR")
                .isSurge(surge)
                .metadata(Map.of("source", "mock"))
                .build();
    }

    private double pseudoDistance(String a, String b) {
        return Math.max(1, Math.abs(a.hashCode() - b.hashCode()) % 25);
    }

    private double random(double min, double max) {
        return new Random().nextDouble() * (max - min) + min;
    }
}
