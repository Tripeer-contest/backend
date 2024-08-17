package com.j10d207.tripeer.plan.db.dto;

import com.j10d207.tripeer.place.db.ContentTypeEnum;
import com.j10d207.tripeer.plan.db.entity.PlanDetailEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class PlanDetailResDTO {

    private long planDetailId;
    private String title;
    private String contentType;
    private int day;
    private int step;
    private LocalTime spotTime;
    private String description;
    private String movingRoot;
    private int cost;

    public static List<PlanDetailResDTO> EntityToDTO (List<PlanDetailEntity> planDetailEntityList) {
        List<PlanDetailResDTO> planDetailResDTOList = new ArrayList<>();
        for (PlanDetailEntity planDetailEntity : planDetailEntityList) {
            //정렬해서 가져온 리스트를 DTO에 저장
            PlanDetailResDTO planDetailResDTO = PlanDetailResDTO.builder()
                    .planDetailId(planDetailEntity.getPlanDetailId())
                    .title(planDetailEntity.getSpotInfo().getTitle())
                    .contentType(ContentTypeEnum.getNameByCode(planDetailEntity.getSpotInfo().getContentTypeId()))
                    .day(planDetailEntity.getDay())
                    .step(planDetailEntity.getStep())
                    .spotTime(planDetailEntity.getSpotTime())
                    .description(planDetailEntity.getDescription())
                    .cost(planDetailEntity.getCost())
                    .build();
            planDetailResDTOList.add(planDetailResDTO);
        }

        return planDetailResDTOList;
    }
}
