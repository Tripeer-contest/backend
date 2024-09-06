package com.j10d207.tripeer.place.db.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendDTO {
	private String keyword;
	private String comment;
	private List<SpotInfoDto> spotInfoDtos;


}
