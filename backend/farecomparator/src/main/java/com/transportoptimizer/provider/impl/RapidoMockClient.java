package com.transportoptimizer.provider.impl;

import com.transportoptimizer.entity.ProviderFare;
import com.transportoptimizer.provider.ProviderClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class RapidoMockClient implements ProviderClient {

    @Override
    public String providerId() {
        return "rapido-mock";
    }

    @Override
    public String providerName() {
        return "Rapido (Mock)";
    }

    @Override
    public ProviderFare getFare(String origin, String destination) {
        return getFaresBatch(origin, destination, null).get(0);
    }

    @Override
    public List<ProviderFare> getFaresBatch(String origin, String destination, Map<String, Object> options) {
        double distance = pseudoDistance(origin, destination);

        return List.of(
                buildFare("Rapido Bike", "bike", distance, 6, 3, 6),
                buildFare("Rapido Auto", "auto", distance, 7, 4, 8)
        );
    }

    private ProviderFare buildFare(String name, String vehicleType, double distance,
                                   double rate, int minEta, int maxEta) {

        double surgeFactor = random(0.9, 1.3);
        boolean surge = surgeFactor > 1.2;

        return ProviderFare.builder()
                .providerId(providerId())
                .providerName(name)
                .vehicleType(vehicleType)
                .distanceKm(distance)
                .price(distance * rate * surgeFactor)
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
