package com.j10d207.tripeer.history.dto.req;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class PlanSaveReq {
	private long planId;
	private List<List<PlanDetailSaveReq>> planDayList;
}
