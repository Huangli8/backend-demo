package com.saygobackend.ai.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AiTripDay {
    private int dayNumber;
    private String notes;
    private List<AiTripActivity> activities;
}

