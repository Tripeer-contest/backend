package com.j10d207.tripeer.history.service;

import java.util.List;

import com.j10d207.tripeer.history.dto.req.CostReq;
import com.j10d207.tripeer.history.dto.req.PlanSaveReq;
import com.j10d207.tripeer.history.dto.res.CostRes;
import com.j10d207.tripeer.history.dto.res.HistoryDetailRes;
import com.j10d207.tripeer.history.dto.res.PlanInfoRes;

public interface HistoryService {

	public List<PlanInfoRes> historyList(long userId);

	public CostRes postCost(CostReq costReq);

	public String savePlanDetail(PlanSaveReq planSaveReq);

	public HistoryDetailRes getHistoryDetail(long planId);

	public String revokeHistoryDetail(long planId);

	public String deleteHistoryDetail(long planId, long userId);
}
