package com.j10d207.tripeer.plan.db.entity;

import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import com.j10d207.tripeer.plan.db.vo.PlanDetailVO;
import com.j10d207.tripeer.tmap.db.entity.PublicRootEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity(name = "plan_detail")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // PK
    private long planDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLAN_DAY_ID")
    private PlanDayEntity planDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPOT_INFO_ID")
    private SpotInfoEntity spotInfo;

    //일자 ex. 1일차 2일차 ..
    private int day;
    //일정 순서
    private int step;
    private LocalTime spotTime;
    //메모
    private String description;
    //비용
    @Setter
    private int cost;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PUBLIC_ROOT_ID")
    private PublicRootEntity publicRoot;

    public static PlanDetailEntity VOToEntity (PlanDetailVO planDetailVO) {
        return PlanDetailEntity.builder()
                .planDetailId(planDetailVO.getPlanDetailId())
                .planDay(PlanDayEntity.builder()
                        .planDayId(planDetailVO.getPlanDayId())
                        .build())
                .spotInfo(SpotInfoEntity.builder()
                        .spotInfoId(planDetailVO.getSpotInfoId())
                        .build())
                .day(planDetailVO.getDay())
                .spotTime(planDetailVO.getSpotTime())
                .step(planDetailVO.getStep())
                .description(planDetailVO.getDescription())
                .cost(planDetailVO.getCost())
                .build();
    }
}