package com.j10d207.tripeer.place.db.vo;

import java.util.List;

import lombok.Getter;

@Getter
public class RecommendVO {
	private String keyword;
	private String comment;
	private List<Integer> idList;
}
