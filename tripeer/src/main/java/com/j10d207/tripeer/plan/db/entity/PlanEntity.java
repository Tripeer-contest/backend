package com.j10d207.tripeer.plan.db.entity;

import com.j10d207.tripeer.plan.db.dto.PlanDetailMainDTO;
import com.j10d207.tripeer.plan.db.vo.PlanCreateInfoVO;
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

    public static PlanEntity VOToEntity (PlanCreateInfoVO createInfo) {
        return PlanEntity.builder()
                .title(createInfo.getTitle())
                .vehicle(createInfo.getVehicle())
                .startDate(createInfo.getStartDay())
                .endDate(createInfo.getEndDay())
                .createDate(LocalDate.now(ZoneId.of("Asia/Seoul")))
                .build();
    }
}
