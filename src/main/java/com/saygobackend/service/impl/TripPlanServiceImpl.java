package com.saygobackend.service.impl;

import com.saygobackend.ai.AiTripClient;
import com.saygobackend.ai.model.AiTripPlan;
import com.saygobackend.ai.model.AiTripDay;
import com.saygobackend.ai.model.AiTripActivity;
import com.saygobackend.dto.GenerateTripPlanRequest;
import com.saygobackend.dto.TripPlanResponse;
import com.saygobackend.dto.TripDayDto;
import com.saygobackend.dto.TripActivityDto;
import com.saygobackend.entity.Destination;
import com.saygobackend.exception.InvalidBudgetRangeException;
import com.saygobackend.exception.InvalidDateRangeException;
import com.saygobackend.repository.DestinationRepository;
import com.saygobackend.repository.TripPlanRepository;
import com.saygobackend.entity.TripPlan;
import com.saygobackend.entity.TripDay;
import com.saygobackend.entity.TripActivity;
import com.saygobackend.service.TripPlanService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripPlanServiceImpl implements TripPlanService {
    private final AiTripClient aiTripClient;
    private final TripPlanRepository tripPlanRepository;
    private final DestinationRepository destinationRepository;

    @Override
    @Transactional
    public List<Long> generatePlans(Long userId, GenerateTripPlanRequest req, boolean multiPlans) {
        validateRequest(req);
        // ✅ 把目的地文字转成数据库里的 ID
        Long destinationId = resolveDestinationId(req.getDestination());

        int planCount = multiPlans ? 3 : 1;
        List<String> planTypes = multiPlans ? Arrays.asList("性价比", "轻松", "本地特色") : Collections.singletonList("性价比");
        List<AiTripPlan> aiPlans = aiTripClient.generatePlans(req, planCount, planTypes);
        List<Long> planIds = new ArrayList<>();
        for (AiTripPlan aiPlan : aiPlans) {
            TripPlan plan = TripPlan.builder()
                    .userId(userId)
                    .destinationId(destinationId)
                    .startDate(req.getStartDate())
                    .endDate(req.getEndDate())
                    .budgetMin(req.getBudgetMin())
                    .budgetMax(req.getBudgetMax())
                    .planType(aiPlan.getPlanType())
                    .createdAt(LocalDate.now().atStartOfDay())
                    .build();
            List<TripDay> dayEntities = new ArrayList<>();
            BigDecimal totalCost = BigDecimal.ZERO;
            if (!CollectionUtils.isEmpty(aiPlan.getDays())) {
                for (AiTripDay aiDay : aiPlan.getDays()) {
                    TripDay day = TripDay.builder()
                            .tripPlan(plan)
                            .dayNumber(aiDay.getDayNumber())
                            .notes(aiDay.getNotes())
                            .build();
                    List<TripActivity> activityEntities = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(aiDay.getActivities())) {
                        for (AiTripActivity aiActivity : aiDay.getActivities()) {
                            TripActivity.TimeSlot slot = parseTimeSlot(aiActivity.getTimeSlot());
                            TripActivity activity = TripActivity.builder()
                                    .tripDay(day)
                                    .activityType(aiActivity.getActivityType())
                                    .referenceId(null)
                                    .description(aiActivity.getDescription())
                                    .estimatedCost(aiActivity.getEstimatedCost())
                                    .timeSlot(slot)
                                    .build();
                            activityEntities.add(activity);
                            if (aiActivity.getEstimatedCost() != null) {
                                totalCost = totalCost.add(aiActivity.getEstimatedCost());
                            }
                        }
                        day.setActivities(activityEntities);
                    }
                    dayEntities.add(day);
                }
                plan.setTripDays(dayEntities);
            }
            plan.setTotalEstimatedCost(aiPlan.getTotalEstimatedCost() != null ? aiPlan.getTotalEstimatedCost() : totalCost);
            TripPlan savedPlan = tripPlanRepository.save(plan);
            planIds.add(savedPlan.getId());
        }
        return planIds;
    }

    @Override
    public TripPlanResponse getPlan(Long planId) {
        TripPlan plan = tripPlanRepository.findById(planId).orElse(null);
        if (plan == null) return null;
        List<TripDayDto> dayDtos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(plan.getTripDays())) {
            for (TripDay day : plan.getTripDays()) {
                List<TripActivityDto> activityDtos = new ArrayList<>();
                if (!CollectionUtils.isEmpty(day.getActivities())) {
                    for (TripActivity activity : day.getActivities()) {
                        activityDtos.add(TripActivityDto.builder()
                                .activityType(activity.getActivityType())
                                .description(activity.getDescription())
                                .estimatedCost(activity.getEstimatedCost())
                                .timeSlot(activity.getTimeSlot() != null ? activity.getTimeSlot().name().toLowerCase() : null)
                                .build());
                    }
                }
                dayDtos.add(TripDayDto.builder()
                        .dayNumber(day.getDayNumber())
                        .notes(day.getNotes())
                        .activities(activityDtos)
                        .build());
            }
        }
        return TripPlanResponse.builder()
                .planId(plan.getId())
                .destination(plan.getDestinationEntity() != null ? plan.getDestinationEntity().getName() : null) // ✅
                .startDate(plan.getStartDate())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .totalEstimatedCost(plan.getTotalEstimatedCost())
                .planType(plan.getPlanType())
                .days(dayDtos)
                .build();
    }

    private void validateRequest(GenerateTripPlanRequest req) {
        if (req.getStartDate() == null || req.getEndDate() == null || req.getStartDate().isAfter(req.getEndDate())) {
            throw new InvalidDateRangeException("出行日期区间不合法");
        }
        if (req.getBudgetMin() == null || req.getBudgetMax() == null || req.getBudgetMin().compareTo(req.getBudgetMax()) > 0) {
            throw new InvalidBudgetRangeException("预算范围不合法");
        }
    }

    private TripActivity.TimeSlot parseTimeSlot(String slot) {
        if (slot == null) return null;
        switch (slot.toLowerCase()) {
            case "morning": return TripActivity.TimeSlot.MORNING;
            case "afternoon": return TripActivity.TimeSlot.AFTERNOON;
            case "evening": return TripActivity.TimeSlot.EVENING;
            default: return null;
        }
    }

    private Long resolveDestinationId(String destinationName) {
        return destinationRepository.findByName(destinationName)
                .orElseGet(() -> {
                    Destination newDest = Destination.builder()
                            .name(destinationName)
                            .country("未知") // 以后可以改成 AI 推断 or 用户输入
                            .build();
                    return destinationRepository.save(newDest);
                })
                .getId();
    }
}
