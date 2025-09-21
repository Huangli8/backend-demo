package com.saygobackend.ai.impl;

import com.saygobackend.ai.AiTripClient;
import com.saygobackend.ai.model.AiTripActivity;
import com.saygobackend.ai.model.AiTripDay;
import com.saygobackend.ai.model.AiTripPlan;
import com.saygobackend.dto.GenerateTripPlanRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Component
public class DummyAiTripClient implements AiTripClient {
    @Override
    public List<AiTripPlan> generatePlans(GenerateTripPlanRequest req, int planCount, List<String> planTypes) {
        List<AiTripPlan> plans = new ArrayList<>();
        for (int i = 0; i < planCount; i++) {
            String type = planTypes != null && planTypes.size() > i ? planTypes.get(i) : "性价比";
            plans.add(mockPlan(req, type));
        }
        return plans;
    }

    private AiTripPlan mockPlan(GenerateTripPlanRequest req, String planType) {
        List<AiTripDay> days = new ArrayList<>();
        LocalDate date = req.getStartDate();
        int dayNum = 1;
        while (!date.isAfter(req.getEndDate())) {
            days.add(mockDay(dayNum, date));
            date = date.plusDays(1);
            dayNum++;
        }
        BigDecimal totalCost = BigDecimal.valueOf(days.size() * 1000L);
        return AiTripPlan.builder()
                .planType(planType)
                .totalEstimatedCost(totalCost)
                .days(days)
                .build();
    }

    private AiTripDay mockDay(int dayNumber, LocalDate date) {
        List<AiTripActivity> activities = new ArrayList<>();
        activities.add(mockActivity("景点参观", "参观著名景点", BigDecimal.valueOf(300), "morning"));
        activities.add(mockActivity("美食体验", "品尝当地美食", BigDecimal.valueOf(200), "afternoon"));
        activities.add(mockActivity("休闲娱乐", "自由活动", BigDecimal.valueOf(100), "evening"));
        return AiTripDay.builder()
                .dayNumber(dayNumber)
                .notes("第" + dayNumber + "天行程，日期: " + date)
                .activities(activities)
                .build();
    }

    private AiTripActivity mockActivity(String type, String desc, BigDecimal cost, String slot) {
        return AiTripActivity.builder()
                .activityType(type)
                .description(desc)
                .estimatedCost(cost)
                .timeSlot(slot)
                .build();
    }
}
