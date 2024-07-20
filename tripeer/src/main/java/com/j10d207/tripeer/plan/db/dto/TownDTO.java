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

        } else {

        }
        /*
        if(planTownEntity.getTown() == null) {
                TownDTO townDTO = TownDTO.builder()
                        .cityId(planTownEntity.getCityOnly().getCityId())
                        .title(planTownEntity.getCityOnly().getCityName())
                        .description(planTownEntity.getCityOnly().getDescription())
                        .img(planTownEntity.getCityOnly().getCityImg())
                        .latitude(planTownEntity.getCityOnly().getLatitude())
                        .longitude(planTownEntity.getCityOnly().getLongitude())
                        .build();
                townDTOList.add(townDTO);
            } else {
                TownDTO townDTO = TownDTO.builder()
                        .cityId(planTownEntity.getTown().getTownPK().getCity().getCityId())
                        .townId(planTownEntity.getTown().getTownPK().getTownId())
                        .title(planTownEntity.getTown().getTownName())
                        .description(planTownEntity.getTown().getDescription())
                        .img(planTownEntity.getTown().getTownImg())
                        .latitude(planTownEntity.getTown().getLatitude())
                        .longitude(planTownEntity.getTown().getLongitude())
                        .build();
                townDTOList.add(townDTO);
            }
         */
    }
}
