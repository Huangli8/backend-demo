package com.saygobackend.service;

import com.saygobackend.dto.GenerateTripPlanRequest;
import com.saygobackend.dto.TripPlanResponse;
import java.util.List;

public interface TripPlanService {
    List<Long> generatePlans(Long userId, GenerateTripPlanRequest req, boolean multiPlans);
    TripPlanResponse getPlan(Long planId);
}

