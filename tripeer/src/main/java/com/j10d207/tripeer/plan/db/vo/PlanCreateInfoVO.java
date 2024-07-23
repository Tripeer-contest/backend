package com.j10d207.tripeer.plan.db.vo;

import com.j10d207.tripeer.plan.db.dto.TownDTO;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class PlanCreateInfoVO {
    private String title;
    private List<TownDTO> townList;
    private String vehicle;
    private LocalDate startDay;
    private LocalDate endDay;
}
