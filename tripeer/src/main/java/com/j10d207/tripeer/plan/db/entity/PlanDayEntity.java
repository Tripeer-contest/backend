package com.j10d207.tripeer.plan.db.entity;

import com.j10d207.tripeer.plan.db.dto.PlanDetailMainDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity(name = "plan_day")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanDayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // PK
    private long planDayId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLAN_ID")
    private PlanEntity plan;

    private LocalDate day;
    private LocalTime startTime;
    private String vehicle;

    public static PlanDayEntity MakeDayEntity(PlanDetailMainDTO.CreateResultInfo createResultInfo, PlanEntity planEntity, int i) {
        return PlanDayEntity.builder()
                .plan(planEntity)
                .day(createResultInfo.getStartDay().plusDays(i))
                .vehicle(createResultInfo.getVehicle())
                .build();

    }
}
