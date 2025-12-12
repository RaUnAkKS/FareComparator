package com.transportoptimizer.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CompareRequestDTO {
    private String userId;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;  // ISO string, optional
    private boolean preferCheapest;
    private boolean preferFastest;
}
