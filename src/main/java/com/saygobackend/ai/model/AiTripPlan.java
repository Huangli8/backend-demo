package com.saygobackend.ai.model;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class AiTripPlan {
    private String planType;
    private BigDecimal totalEstimatedCost;
    private List<AiTripDay> days;
}

