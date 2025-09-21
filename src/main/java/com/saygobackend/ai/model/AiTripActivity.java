package com.saygobackend.ai.model;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class AiTripActivity {
    private String activityType;
    private String description;
    private BigDecimal estimatedCost;
    private String timeSlot; // morning/afternoon/evening
}

