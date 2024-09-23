package com.j10d207.tripeer.plan.db.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.j10d207.tripeer.history.db.entity.GalleryEntity;

import com.j10d207.tripeer.plan.dto.res.PlanDetailMainDTO;
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

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLAN_DAY_ID")
    private List<PlanDetailEntity> planDetailList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLAN_DAY_ID")
    private List<GalleryEntity> galleryList;

    public static PlanDayEntity createEntity(PlanDetailMainDTO.CreateResultInfo createResultInfo, PlanEntity planEntity, int i) {
        return PlanDayEntity.builder()
                .plan(planEntity)
                .day(createResultInfo.getStartDay().plusDays(i))
                .build();

    }


	// 저장을 위해 planEntity 만으로 플랜에 맞는 길이의 PlanDayEntity 리스트를 생성
	public static List<PlanDayEntity> from(PlanEntity planEntity) {
		int howLongDays = (int) ChronoUnit.DAYS.between(planEntity.getStartDate(), planEntity.getEndDate());
		return IntStream.range(0, howLongDays + 1)
			.mapToObj(i -> PlanDayEntity.builder()
				.plan(planEntity)
				.day(planEntity.getStartDate().plusDays(i))
				.build())
			.toList();
	}
}
