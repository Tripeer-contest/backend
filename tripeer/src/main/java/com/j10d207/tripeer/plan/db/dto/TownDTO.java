package com.j10d207.tripeer.plan.db.dto;

import com.j10d207.tripeer.plan.db.entity.PlanTownEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TownDTO {

    private int cityId;
    private int townId;
    private String title;
    private String description;
    private String img;
    private double latitude;
    private double longitude;

    public static TownDTO fromPlanTownEntity(PlanTownEntity planTown) {
        // 도가 선택된경우 , ex. 서울특별시, 경상북도
        if (planTown.getTown() == null) {
            return TownDTO.builder()
                    .cityId(planTown.getCityOnly().getCityId())
                    .cityId(planTown.getCityOnly().getCityId())
                    .title(planTown.getCityOnly().getCityName())
                    .description(planTown.getCityOnly().getDescription())
                    .img(planTown.getCityOnly().getCityImg())
                    .latitude(planTown.getCityOnly().getLatitude())
                    .longitude(planTown.getCityOnly().getLongitude())
                    .build();

            // 더 좁은범위의 도시가 선택된경우, ex. 강서구, 무주군
        } else {
            return TownDTO.builder()
                    .cityId(planTown.getTown().getTownPK().getCity().getCityId())
                    .townId(planTown.getTown().getTownPK().getTownId())
                    .title(planTown.getTown().getTownName())
                    .description(planTown.getTown().getDescription())
                    .img(planTown.getTown().getTownImg())
                    .latitude(planTown.getTown().getLatitude())
                    .longitude(planTown.getTown().getLongitude())
                    .build();
        }
    }
}
