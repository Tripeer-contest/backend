package com.j10d207.tripeer.history.db.dto;

import com.j10d207.tripeer.plan.dto.res.PlanListResDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class HistoryDetailResDTO {
    private PlanListResDTO diaryDetail;
    private List<HistoryDayDTO> diaryDayList;
    private long plan_id;
    private List<Map<String, Integer>> cityIdTownIdList;
}
