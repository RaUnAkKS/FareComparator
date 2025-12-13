package com.transportoptimizer.provider;

import com.transportoptimizer.entity.ProviderFare;

import java.util.List;
import java.util.Map;

/**
 * ProviderClient defines the contract for any fare provider integration.
 */
public interface ProviderClient {

    String providerId();
    String providerName();

    /**
     * Fetches a single fare estimate using precomputed distance.
     */
    ProviderFare getFare(String origin, String destination, double distanceKm);

    /**
     * Backward compatibility (optional)
     */
    default ProviderFare getFare(String origin, String destination) {
        return getFare(origin, destination, 0);
    }

    /**
     * Batch mode (optional)
     */
    default List<ProviderFare> getFaresBatch(
            String origin,
            String destination,
            double distanceKm,
            Map<String, Object> options
    ) {
        return List.of(getFare(origin, destination, distanceKm));
    }

    /**
     * Backward compatibility
     */
    default List<ProviderFare> getFaresBatch(
            String origin,
            String destination,
            Map<String, Object> options
    ) {
        return getFaresBatch(origin, destination, 0, options);
    }
}
