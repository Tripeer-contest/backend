package com.j10d207.tripeer.history.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CostResDTO {
	private long planDetailId;
	private int cost;
}
