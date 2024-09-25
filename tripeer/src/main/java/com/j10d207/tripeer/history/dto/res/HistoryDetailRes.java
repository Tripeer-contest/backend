package com.j10d207.tripeer.history.dto.res;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j10d207.tripeer.plan.db.entity.PlanEntity;
import com.j10d207.tripeer.plan.db.entity.PlanTownEntity;
import com.j10d207.tripeer.user.db.entity.UserEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HistoryDetailRes {
	// 해당 플랜 id
	private long planId;
	// 지난 여행 상단의 해당 여행의 간단한 소개
	private PlanInfoRes diaryDetail;
	// 각 날짜 별로 어디에 갔는지에 대한 소개
	private List<HistoryDayRes> diaryDayList;
	// 해당 여행의 장소를 리스트로 반환 ex) {cityId : 12, townId : 123}
	private List<Map<String, Integer>> cityIdTownIdList;

	public static HistoryDetailRes from(PlanEntity planEntity, UserEntity userEntity) {
		return HistoryDetailRes.builder()
			.planId(planEntity.getPlanId())
			.diaryDetail(PlanInfoRes.from(planEntity))
			.diaryDayList(planEntity.getPlanDayList().stream().map(el -> HistoryDayRes.from(el,userEntity)).toList())
			.cityIdTownIdList(makeCityIdTownIdList(planEntity))
			.build();
	}

	// 현재는 임시로 함수로 만들었지만 db저장 방식을 손보든지 해서 고쳐야할거 같다.
	private static List<Map<String, Integer>> makeCityIdTownIdList(PlanEntity planEntity) {
		List<PlanTownEntity> planTown = planEntity.getPlanTown();
		List<Map<String, Integer>> cityTownIdList = new ArrayList<>();
		for (
			PlanTownEntity planTownEntity : planTown) {
			Map<String, Integer> cityTownMap = new HashMap<>();
			if (planTownEntity.getCityOnly() == null) {
				cityTownMap.put("cityId", planTownEntity.getTown().getTownPK().getCity().getCityId());
				cityTownMap.put("townId", planTownEntity.getTown().getTownPK().getTownId());

			} else {
				cityTownMap.put("cityId", planTownEntity.getCityOnly().getCityId());
				cityTownMap.put("townId", -1);
			}

			if (planTownEntity.getTown() == null) {
				cityTownMap.put("townId", -1);
			} else {
				cityTownMap.put("townId", planTownEntity.getTown().getTownPK().getTownId());
			}
			cityTownIdList.add(cityTownMap);
		}
		return cityTownIdList;
	}

}
