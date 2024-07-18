package com.j10d207.tripeer.history.service;

import com.j10d207.tripeer.history.dto.request.CostReqDTO;
import com.j10d207.tripeer.history.dto.response.CostResDTO;
import com.j10d207.tripeer.history.dto.response.HistoryDetailResDTO;
import com.j10d207.tripeer.history.dto.request.PlanSaveReqDTO;
import com.j10d207.tripeer.plan.db.dto.PlanListResDTO;

import java.util.List;

public interface HistoryService {

    public List<PlanListResDTO> historyList(String token);

    public CostResDTO postCost(CostReqDTO costReqDTO);

    public String savePlanDetail(PlanSaveReqDTO planSaveReqDTO);

    public HistoryDetailResDTO getHistoryDetail(long planId);

    public String revokeHistoryDetail(long planId);
}
