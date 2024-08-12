package com.j10d207.tripeer.history.service;

import com.j10d207.tripeer.history.db.dto.CostReqDTO;
import com.j10d207.tripeer.history.db.dto.CostResDTO;
import com.j10d207.tripeer.history.db.dto.HistoryDetailResDTO;
import com.j10d207.tripeer.history.db.dto.PlanSaveReqDTO;
import com.j10d207.tripeer.plan.dto.res.PlanListResDTO;

import java.util.List;

public interface HistoryService {

    public List<PlanListResDTO> historyList(long userId);

    public CostResDTO postCost(CostReqDTO costReqDTO);

    public String savePlanDetail(PlanSaveReqDTO planSaveReqDTO);

    public HistoryDetailResDTO getHistoryDetail(long planId);

    public String revokeHistoryDetail(long planId);
}
