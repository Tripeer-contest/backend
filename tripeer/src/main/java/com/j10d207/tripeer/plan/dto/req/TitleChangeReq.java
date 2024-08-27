package com.j10d207.tripeer.plan.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class TitleChangeReq {

    private long planId;
    @NotBlank(message = "제목에 아무것도 입력되지 않았습니다. (공백포함) ")
    @Size(min = 2, max = 10, message = "2글자 미만이거나 10글자가 초과된 값이 입력되었습니다.")
    private String title;
}
