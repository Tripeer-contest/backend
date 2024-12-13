package com.j10d207.tripeer.place.dto.req;

import java.util.List;

import lombok.Getter;

@Getter
public class RecommendReq {
	private String keyword;
	private String comment;
	private List<Integer> idList;
}
