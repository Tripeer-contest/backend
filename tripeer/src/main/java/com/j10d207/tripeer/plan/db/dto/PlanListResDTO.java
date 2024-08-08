package com.j10d207.tripeer.plan.db.dto;

import com.j10d207.tripeer.user.db.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PlanListResDTO {

    private long planId;
    private String title;
    private String img;
    private List<String> townList;
    private LocalDate startDay;
    private LocalDate endDay;
    private List<UserDTO.Search> member;
    private boolean newPlan;

}
