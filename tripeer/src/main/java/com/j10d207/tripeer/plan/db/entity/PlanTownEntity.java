package com.j10d207.tripeer.plan.db.entity;

import com.j10d207.tripeer.place.db.entity.CityEntity;
import com.j10d207.tripeer.place.db.entity.TownEntity;
import com.j10d207.tripeer.place.db.entity.TownPK;
import com.j10d207.tripeer.plan.db.dto.TownDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "plan_town")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlanTownEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // PK
    private long planTownId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLAN_ID")
    private PlanEntity plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns( {@JoinColumn(name = "CITY_ID" ), @JoinColumn(name = "TOWN_ID")} )
    private TownEntity town;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CITY_ONLY_ID")
    private CityEntity cityOnly;


    public static List<String> ConvertToNameList (List<PlanTownEntity> planTown) {
        List<String> townNameList = new ArrayList<>();
        for(PlanTownEntity planTownEntity : planTown) {
            if(planTownEntity.getTown() == null) {
                townNameList.add(planTownEntity.getCityOnly().getCityName());
            } else {
                townNameList.add(planTownEntity.getTown().getTownName());
            }
        }

        return townNameList;
    }

    public static PlanTownEntity ofDtoAndPlanEntity(TownDTO townDTO, PlanEntity planEntity)
    {
        PlanTownEntity planTown;
        if(townDTO.getTownId() == -1) {
            planTown = PlanTownEntity.builder()
                    .plan(planEntity)
                    .cityOnly(CityEntity.builder().cityId(townDTO.getCityId()).build())
                    .build();
        } else {
            TownPK townPK = TownPK.builder()
                    .townId(townDTO.getTownId())
                    .city(CityEntity.builder().cityId(townDTO.getCityId()).build())
                    .build();
            planTown = PlanTownEntity.builder()
                    .plan(planEntity)
                    .town(TownEntity.builder().townPK(townPK).build())
                    .build();
        }
        return planTown;
    }

    public static String getFirstImg (List<PlanTownEntity> planTownEntityList) {
        if(planTownEntityList.getFirst().getTown() == null ) {
            return planTownEntityList.getFirst().getCityOnly().getCityImg();
        } else {
            return planTownEntityList.getFirst().getTown().getTownImg();
        }
    }
}
