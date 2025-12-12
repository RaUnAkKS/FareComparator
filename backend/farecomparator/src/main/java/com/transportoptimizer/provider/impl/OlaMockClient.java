package com.transportoptimizer.provider.impl;

import com.transportoptimizer.entity.ProviderFare;
import com.transportoptimizer.provider.ProviderClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class OlaMockClient implements ProviderClient {

    @Override
    public String providerId() {
        return "ola-mock";
    }

    @Override
    public String providerName() {
        return "Ola (Mock)";
    }

    @Override
    public ProviderFare getFare(String origin, String destination) {
        return getFaresBatch(origin, destination, null).get(0);
    }

    @Override
    public List<ProviderFare> getFaresBatch(String origin, String destination, Map<String, Object> options) {
        double distance = pseudoDistance(origin, destination);

        return List.of(
                buildFare("Ola Mini", "cab", distance, 11, 7, 14),
                buildFare("Ola Prime Sedan", "premium_cab", distance, 17, 6, 12),
                buildFare("Ola Auto", "auto", distance, 8, 4, 8)
        );
    }

    private ProviderFare buildFare(String vehicleName, String vehicleType, double distance, double rate,
                                   int minEta, int maxEta) {

        double base = distance * rate;
        double surgeFactor = random(0.85, 1.55);
        boolean surge = surgeFactor > 1.3;

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
