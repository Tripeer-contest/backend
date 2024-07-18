package com.j10d207.tripeer.history.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HistoryDayDTO {
	private long planDayId;
	private String date;
	private Integer day;
	// 각 날짜 별로 4개의 미리보기 이미지
	private List<String> galleryImgs;
	// 각 날짜 별로 어느 관광지에 갔었는지의 리스트
	private List<HistorySpotResDTO> planDetailList;
	// 각 여행지 사이의 이동 시간 리스트
	private List<List<String>> timeList;
	// 각 여행지 사이를 어떻게 갔는지에 대한 경로 정보
	private List<RouteDTO> routeList;
}
