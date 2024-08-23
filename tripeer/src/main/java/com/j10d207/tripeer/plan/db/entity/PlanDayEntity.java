package com.j10d207.tripeer.plan.db.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.j10d207.tripeer.history.db.entity.GalleryEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public static PlanDayEntity CreateEntity (PlanDetailMainDTO.CreateResultInfo createResultInfo, PlanEntity planEntity, int i) {
        return PlanDayEntity.builder()
                .plan(planEntity)
                .day(createResultInfo.getStartDay().plusDays(i))
                .vehicle(createResultInfo.getVehicle())
                .build();

    }
}
