package com.j10d207.tripeer.place.dto.res;

import java.util.List;

import com.j10d207.tripeer.plan.dto.res.SpotSearchResDTO;

import lombok.Builder;
import lombok.Getter;

// 플랜에서 추천받을때 사용하는 DTO
// 플랜이 버킷에 있는지 검사해야함
@Getter
@Builder
public class RecommendSearchDTO {
	private String keyword;
	private String comment;
	private List<SpotSearchResDTO.SearchResult> spotInfoDtos;
}
