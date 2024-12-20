package com.j10d207.tripeer.plan.db.entity;

import com.j10d207.tripeer.plan.dto.res.PlanDetailMainDTO;
import com.j10d207.tripeer.user.db.entity.CoworkerEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "plan")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// PK
	private long planId;
	@Setter
	private String title;
	@Setter
	private Boolean isSaved;
	private LocalDate startDate;
	private LocalDate endDate;
	private LocalDate createDate;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "PLAN_ID")
	@OrderBy("day ASC")
	Set<PlanDayEntity> planDayList;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "PLAN_ID")
	private List<PlanTownEntity> planTown;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "PLAN_ID")
	private List<CoworkerEntity> coworkerList;

    public static PlanEntity fromDto(PlanDetailMainDTO.CreateResultInfo CreateResultInfo) {
        return PlanEntity.builder()
                .title(CreateResultInfo.getTitle())
                .isSaved(false)
                .startDate(CreateResultInfo.getStartDay())
                .endDate(CreateResultInfo.getEndDay())
                .createDate(LocalDate.now(ZoneId.of("Asia/Seoul")))
                .build();
    }

}
