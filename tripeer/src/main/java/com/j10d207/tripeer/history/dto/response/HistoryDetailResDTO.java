package com.j10d207.tripeer.history.dto.response;

import com.j10d207.tripeer.plan.db.dto.PlanListResDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class HistoryDetailResDTO {
	// 해당 플랜 id
	private long planId;
	// 지난 여행 상단의 해당 여행의 간단한 소개
	private PlanListResDTO diaryDetail;
	// 각 날짜 별로 어디에 갔는지에 대한 소개
	private List<HistoryDayDTO> diaryDayList;
	// 해당 여행의 장소를 리스트로 반환 ex) {cityId : 12, townId : 123}
	private List<Map<String, Integer>> cityIdTownIdList;
}
