package com.transportoptimizer.provider;

import com.transportoptimizer.entity.ProviderFare;

import java.util.List;
import java.util.Map;

/**
 * ProviderClient defines the contract for any fare provider integration
 * (e.g., Uber, Ola, Rapido, public transit APIs, etc.).
 * Each provider implementation must supply its ID, name,
 * and logic to fetch fare estimates.
 */
public interface ProviderClient {

    /**
     * Returns the unique provider identifier.
     * This ID should remain stable and be used internally to distinguish providers.
     *
     * @return provider's unique ID
     */
    String providerId();

    /**
     * Returns the display-friendly provider name.
     * This name may be shown to users in the frontend response.
     *
     * @return provider's display name
     */
    String providerName();

    /**
     * Fetches a single fare estimate between the given origin and destination.
     * Implementations should call the provider’s API, parse the response,
     * and return a populated ProviderFare object.
     *
     * @param origin      the start location
     * @param destination the end location
     * @return ProviderFare containing price, ETA, surge, etc.
     */
    ProviderFare getFare(String origin, String destination);

    /**
     * Fetches multiple fare estimates in batch mode.
     * Implementations may use provider-specific options such as ride type,
     * time of day, mode preference, or API flags provided in the options map.
     *
     * @param origin      start location
     * @param destination end location
     * @param options     optional additional parameters for provider API calls
     * @return list of ProviderFare results for the given query
     */
    List<ProviderFare> getFaresBatch(String origin, String destination, Map<String, Object> options);
}
