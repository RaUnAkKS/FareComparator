package com.transportoptimizer.Controller;

import com.transportoptimizer.entity.FareHistory;
import com.transportoptimizer.Repository.FareHistoryRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final FareHistoryRepository fareHistoryRepository;

    @GetMapping("/trends")
    public Map<String, Object> trends(@RequestParam String userId) {

        List<FareHistory> history = fareHistoryRepository
                .findByUserIdOrderByCreatedAtDesc(userId);

        Map<String, Long> countPerDay = history.stream()
                .collect(Collectors.groupingBy(
                        h -> h.getCreatedAt().toString().substring(0, 10),   // yyyy-mm-dd
                        Collectors.counting()
                ));

        List<String> labels = new ArrayList<>(countPerDay.keySet());
        Collections.sort(labels);

        List<Long> data = labels.stream()
                .map(countPerDay::get)
                .collect(Collectors.toList());

        return Map.of(
                "labels", labels,
                "requestCountPerDay", data
        );
    }

    @GetMapping("/savings-trend")
    public Map<String, Object> savingsTrend(@RequestParam String userId) {

        List<FareHistory> history = fareHistoryRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(h -> h.getSavings() != null)
                .limit(7)
                .collect(Collectors.toList());

        Collections.reverse(history);

        List<String> labels = new ArrayList<>();
        List<Double> savingsList = new ArrayList<>();

        int count = 1;
        for (FareHistory h : history) {
            labels.add("#" + count++);
            savingsList.add(h.getSavings());
        }

        return Map.of(
                "labels", labels,
                "savings", savingsList
        );
    }

    @GetMapping("/summary")
    public Map<String, Object> summary(@RequestParam String userId) {

        List<FareHistory> history = fareHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId);

        if (history.isEmpty()) {
            return Map.of(
                    "totalRequests", 0,
                    "averageSavings", 0.0,
                    "mostUsedProvider", "N/A"
            );
        }

        int totalRequests = history.size();

        double avgSavings = history.stream()
                .filter(h -> h.getSavings() != null)
                .mapToDouble(FareHistory::getSavings)
                .average()
                .orElse(0.0);

        String mostUsedProvider = history.stream()
                .collect(Collectors.groupingBy(
                        FareHistory::getChosenProviderId,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        return Map.of(
                "totalRequests", totalRequests,
                "averageSavings", avgSavings,
                "mostUsedProvider", mostUsedProvider
        );
    }


    // --------------------------------------------------------------
    // ⭐ NEW ENDPOINT (Minimal addition for Provider-wise Savings Graph)
    // --------------------------------------------------------------
    @GetMapping("/provider-savings-trend")
    public Map<String, Object> providerSavingsTrend(@RequestParam String userId) {

        List<FareHistory> history = fareHistoryRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(h -> h.getSavings() != null)
                .collect(Collectors.toList());

        if (history.isEmpty()) {
            return Map.of(
                    "labels", List.of(),
                    "datasets", List.of()
            );
        }

        // oldest → newest
        Collections.reverse(history);

        List<String> labels = new ArrayList<>();
        int c = 1;
        for (int i = 0; i < history.size(); i++) {
            labels.add("#" + c++);
        }

        Map<String, List<Double>> providerSavings = new HashMap<>();
        Map<String, String> providerNames = new HashMap<>();

        int idx = 0;
        for (FareHistory h : history) {
            String pid = h.getChosenProviderId();
            if (pid == null) {
                idx++;
                continue;
            }

            String name = null;

// 1) If chosenFare exists → use providerName
            if (h.getChosenFare() != null &&
                    h.getChosenFare().getProviderName() != null &&
                    !h.getChosenFare().getProviderName().isBlank()) {

                name = h.getChosenFare().getProviderName();
            }

// 2) Else use chosenProviderName (fallback)
            else if (h.getChosenProviderName() != null &&
                    !h.getChosenProviderName().isBlank()) {

                name = h.getChosenProviderName();
            }

// 3) Last fallback → providerId
            else {
                name = h.getChosenProviderId();
            }


            providerSavings.putIfAbsent(pid, new ArrayList<>(Collections.nCopies(labels.size(), null)));
            providerSavings.get(pid).set(idx, h.getSavings());

            providerNames.put(pid, name);
            idx++;
        }

        List<Map<String, Object>> datasets = new ArrayList<>();
        for (String pid : providerSavings.keySet()) {
            datasets.add(Map.of(
                    "label", providerNames.get(pid),
                    "data", providerSavings.get(pid)
            ));
        }

        return Map.of(
                "labels", labels,
                "datasets", datasets
        );
    }
}

