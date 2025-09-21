package com.saygobackend.controller;

import com.saygobackend.dto.GenerateTripPlanRequest;
import com.saygobackend.dto.TripPlanResponse;
import com.saygobackend.service.TripPlanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/trip-plans")
@RequiredArgsConstructor
@Validated
public class TripPlanController {
    private final TripPlanService tripPlanService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateTripPlans(
            @Valid @RequestBody GenerateTripPlanRequest request,
            @RequestParam(value = "multiPlans", defaultValue = "false") boolean multiPlans) {
        try {
            // TODO: 从 SecurityContext 获取 userId，这里暂用 1L
            Long userId = 1L;
            List<Long> planIds = tripPlanService.generatePlans(userId, request, multiPlans);
            return ResponseEntity.ok(planIds);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("生成方案失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTripPlan(@PathVariable("id") Long id) {
        TripPlanResponse response = tripPlanService.getPlan(id);
        if (response == null) {
            return ResponseEntity.badRequest().body("方案不存在");
        }
        return ResponseEntity.ok(response);
    }
}

