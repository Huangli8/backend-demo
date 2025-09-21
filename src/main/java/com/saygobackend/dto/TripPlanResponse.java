package com.saygobackend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class TripPlanResponse {
    private Long planId;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalEstimatedCost;
    private String planType;
    private List<TripDayDto> days;


}
