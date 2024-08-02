package com.j10d207.tripeer.plan.db.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TitleChangeVO {

    private long planId;
    @NotBlank(message = "제목에 아무것도 입력되지 않았습니다. (공백포함) ")
    private String title;
}
