package com.j10d207.tripeer.plan.db.entity;

import java.time.LocalTime;

import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import com.j10d207.tripeer.plan.dto.req.PlanDetailReq;
import com.j10d207.tripeer.tmap.db.entity.PublicRootEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "plan_detail")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanDetailEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// PK
	private long planDetailId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PLAN_DAY_ID")
	private PlanDayEntity planDay;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SPOT_INFO_ID")
	private SpotInfoEntity spotInfo;

	//일자 ex. 1일차 2일차 ..
	private int day;
	//일정 순서
	private int step;
	private LocalTime spotTime;
	//메모
	private String description;
	//비용
	@Setter
	private int cost;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PUBLIC_ROOT_ID")
	private PublicRootEntity publicRoot;

    public static PlanDetailEntity fromReq(PlanDetailReq planDetailReq) {
        return PlanDetailEntity.builder()
                .planDay(PlanDayEntity.builder()
                        .planDayId(planDetailReq.getPlanDayId())
                        .build())
                .spotInfo(SpotInfoEntity.builder()
                        .spotInfoId(planDetailReq.getSpotInfoId())
                        .build())
                .day(planDetailReq.getDay())
                .spotTime(planDetailReq.getSpotTime())
                .step(planDetailReq.getStep())
                .description(planDetailReq.getDescription())
                .cost(planDetailReq.getCost())
                .build();
    }
}
