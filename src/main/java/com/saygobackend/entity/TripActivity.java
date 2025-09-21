package com.saygobackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "trip_activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_day_id", nullable = false)
    private TripDay tripDay;

    @Column(name = "activity_type", nullable = false)
    private String activityType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "description")
    private String description;

    @Column(name = "estimated_cost")
    private BigDecimal estimatedCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_slot", nullable = false)
    private TimeSlot timeSlot;

    public enum TimeSlot {
        MORNING,
        AFTERNOON,
        EVENING
    }
}
