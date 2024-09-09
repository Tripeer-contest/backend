package com.j10d207.tripeer.place.dto.res;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendDTO {
	private String keyword;
	private String comment;
	private List<SpotDTO.SpotInfoDTO> spotInfoDtos;


}
