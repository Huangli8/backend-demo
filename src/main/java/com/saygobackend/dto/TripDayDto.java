package com.saygobackend.dto;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class TripDayDto {
    private Integer dayNumber;
    private String notes;
    private List<TripActivityDto> activities;


}

