package com.j10d207.tripeer.plan.db.vo;

import com.j10d207.tripeer.plan.db.dto.TownDTO;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class PlanCreateInfoVO {

    @NotBlank(message = "제목에 아무것도 입력되지 않았습니다. (공백포함) ")
    @Size(max = 60, message = "60글자가 초과된 값이 입력되었습니다.")
    private String title;
    private List<TownDTO> townList;
    private String vehicle;
    @Future(message = "과거 일자를 계획할 수 없습니다.")
    private LocalDate startDay;
    @Future(message = "과거 일자를 계획할 수 없습니다.")
    private LocalDate endDay;
}
