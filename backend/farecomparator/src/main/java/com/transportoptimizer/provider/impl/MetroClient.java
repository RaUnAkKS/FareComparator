package com.transportoptimizer.provider.impl;

import com.transportoptimizer.entity.ProviderFare;
import com.transportoptimizer.provider.ProviderClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MetroClient implements ProviderClient {

    @Override
    public String providerId() {
        return "metro";
    }

    @Override
    public String providerName() {
        return "Metro";
    }

    @Override
    public ProviderFare getFare(String origin, String destination) {
        return getFaresBatch(origin, destination, null).get(0);
    }

    @Override
    public List<ProviderFare> getFaresBatch(String origin, String destination, Map<String, Object> options) {
        double distance = pseudoDistance(origin, destination);
        double price = 10 + distance * 2;
        int eta = (int) (distance / 0.4);

        ProviderFare fare = ProviderFare.builder()
                .providerId(providerId())
                .providerName("Metro Standard")
                .vehicleType("metro")
                .distanceKm(distance)
                .price(price)
                .etaMinutes(eta)
                .currency("INR")
                .isSurge(false)
                .metadata(Map.of("source", "mock"))
                .build();

        return List.of(fare);
    }

    private double pseudoDistance(String a, String b) {
        return Math.max(1, Math.abs(a.hashCode() - b.hashCode()) % 25);
    }
}
