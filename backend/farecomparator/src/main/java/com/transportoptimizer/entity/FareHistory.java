package com.transportoptimizer.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "fare_history")
public class FareHistory {

    @Id
    private String historyId;

    private String userId;
    private TripRequest tripRequest;
    private FareEstimate fareEstimate;
    private String chosenProviderId;
    private Double savings;
    private Instant createdAt;
}
