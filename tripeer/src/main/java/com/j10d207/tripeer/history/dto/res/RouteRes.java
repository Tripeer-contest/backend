package com.j10d207.tripeer.history.dto.res;

import java.util.List;

import com.j10d207.tripeer.plan.db.entity.PlanDetailEntity;
import com.j10d207.tripeer.tmap.db.entity.PublicRootEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RouteRes {
	private Integer totalFare;
	private Integer pathType;
	// 이동 방법(pathType) 이 대중교동(1) 이었을때 자세한 이동 방법 리스트 (어떤 버스를 타고 얼만큼 걷고 등)
	private List<RouteDetailRes> publicRootDetailList;

	public static RouteRes from(PlanDetailEntity planDetailEntity) {
		// 자동차면 빈 RouteDTO 만들어서 리턴
		if (planDetailEntity.getDescription().equals("자동차")) {
			return RouteRes.builder().build();
			// 대중교통 이라면 이동 방법을 포함한 RouteDTO 만들어서 리턴
		} else {
			PublicRootEntity publicRootEntity = planDetailEntity.getPublicRoot();
			return RouteRes.builder()
				.totalFare(publicRootEntity.getTotalFare())
				.pathType(publicRootEntity.getPathType())
				.publicRootDetailList(
					publicRootEntity.getPublicRootDetailList().stream().map(RouteDetailRes::from).toList())
				.build();
		}
	}
}
