package com.j10d207.tripeer.user.dto.req;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class InfoReq {

    @NotBlank(message = "이메일이 입력되지 않았거나 공백입니다. ${validatedValue}")
    @Email(message = "올바르지 않은 이메일 형식입니다. ${validatedValue}")
    private String email;
    @Size(min = 2, max = 10, message = "닉네임이 2글자 미만이거나, 10글자 이상입니다. ${validatedValue}")
    @NotBlank(message = "닉네임이 입력되지 않았거나 공백입니다. ${validatedValue}")
    private String nickname;
    @Min(value = 1, message = "하나 이상의 관심사를 선택해주세요")
    private int style1Num;
    private int style2Num;
    private int style3Num;
}
