package com.j10d207.tripeer.plan.dto.req;

import com.j10d207.tripeer.plan.db.dto.TownDTO;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class PlanCreateInfoReq {

    @NotBlank(message = "제목에 아무것도 입력되지 않았습니다. (공백포함) ")
    @Size(min = 2, max = 10, message = "2글자 미만이거나 10글자가 초과된 값이 입력되었습니다.")
    private String title;
    @NotNull(message = "선택된 여행지가 없습니다. townList가 null입니다. ")
    private List<TownDTO> townList;
    private Boolean isSaved;
    @Future(message = "과거 일자를 계획할 수 없습니다.")
    private LocalDate startDay;
    @Future(message = "과거 일자를 계획할 수 없습니다.")
    private LocalDate endDay;
}
