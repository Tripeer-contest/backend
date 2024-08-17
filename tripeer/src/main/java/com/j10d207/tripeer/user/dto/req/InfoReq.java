package com.j10d207.tripeer.user.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class InfoReq {

    @Email(message = "올바르지 않은 이메일 형식입니다. ${validatedValue}")
    private String email;
    @Min(2)
    @NotBlank(message = "닉네임이 입력되지 않았거나 공백입니다. ${validatedValue}")
    private String nickname;
    @NotNull(message = "하나 이상의 관심사를 선택해주세요")
    private int style1Num;
    private int style2Num;
    private int style3Num;
}