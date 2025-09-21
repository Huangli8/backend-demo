package com.saygobackend.repository;

import com.saygobackend.entity.TripPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripPlanRepository extends JpaRepository<TripPlan, Long> {
}

