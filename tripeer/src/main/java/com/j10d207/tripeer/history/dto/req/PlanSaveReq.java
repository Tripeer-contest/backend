package com.j10d207.tripeer.history.dto.req;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class PlanSaveReq {
	@Schema(description = "테스트 값", example = "abcd")
	private long planId;
	private List<List<Map<String, String>>> totalYList;
	private List<List<Object>> timeYList;
}
