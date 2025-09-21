package com.saygobackend.dto;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;

@Data
@Builder
public class TripActivityDto {
    private String activityType;
    private String description;
    private BigDecimal estimatedCost;
    private String timeSlot;


}
