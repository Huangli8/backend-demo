package com.saygobackend.ai;

import com.saygobackend.dto.GenerateTripPlanRequest;
import com.saygobackend.ai.model.AiTripPlan;
import java.util.List;

public interface AiTripClient {
    List<AiTripPlan> generatePlans(GenerateTripPlanRequest req, int planCount, List<String> planTypes);
}

