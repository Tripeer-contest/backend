package com.j10d207.tripeer.plan.db.entity;

import com.j10d207.tripeer.place.db.entity.CityEntity;
import com.j10d207.tripeer.place.db.entity.TownEntity;
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
}
