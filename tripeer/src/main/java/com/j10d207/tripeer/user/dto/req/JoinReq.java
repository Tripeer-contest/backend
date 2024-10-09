package com.j10d207.tripeer.user.dto.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class JoinReq {

    @NotBlank(message = "닉네임이 입력되지 않았거나 공백입니다. ${validatedValue}")
    private String nickname;
    @Size(min = 4, max = 4, message = "입력 연도의 형식이 잘못되었습니다. ${validatedValue}")
    @Max(value = 8, message = "지정할 수 있는 관심사의 범위를 초과하였습니다. ${validatedValue}")
    private Integer style1;
    @Max(value = 8, message = "지정할 수 있는 관심사의 범위를 초과하였습니다. ${validatedValue}")
    private Integer style2;
    @Max(value = 8, message = "지정할 수 있는 관심사의 범위를 초과하였습니다. ${validatedValue}")
    private Integer style3;
}
