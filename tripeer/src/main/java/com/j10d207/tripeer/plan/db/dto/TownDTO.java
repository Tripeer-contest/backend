package com.j10d207.tripeer.plan.db.dto;

import com.j10d207.tripeer.place.db.entity.TownEntity;
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

    public static TownDTO EntityToDTO (PlanTownEntity planTown) {
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