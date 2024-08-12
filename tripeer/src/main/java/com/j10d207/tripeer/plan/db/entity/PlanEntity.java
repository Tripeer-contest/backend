package com.j10d207.tripeer.plan.db.entity;

import com.j10d207.tripeer.plan.dto.res.PlanDetailMainDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;
import java.time.ZoneId;

@Entity(name = "plan")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // PK
    private long planId;
    @Setter
    private String title;
    @Setter
    private String vehicle;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate createDate;

    public static PlanEntity DTOToEntity (PlanDetailMainDTO.CreateResultInfo CreateResultInfo) {
        return PlanEntity.builder()
                .title(CreateResultInfo.getTitle())
                .vehicle(CreateResultInfo.getVehicle())
                .startDate(CreateResultInfo.getStartDay())
                .endDate(CreateResultInfo.getEndDay())
                .createDate(LocalDate.now(ZoneId.of("Asia/Seoul")))
                .build();
    }
}
