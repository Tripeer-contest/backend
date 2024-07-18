package com.j10d207.tripeer.history.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RouteDTO {
	private Integer totalFare;
	private Integer pathType;
	// 이동 방법(pathType) 이 대중교동(1) 이었을때 자세한 이동 방법 리스트 (어떤 버스를 타고 얼만큼 걷고 등)
	private List<RouteDetailDTO> publicRootDetailList;
}
