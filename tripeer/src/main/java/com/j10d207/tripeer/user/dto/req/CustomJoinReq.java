package com.j10d207.tripeer.user.dto.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CustomJoinReq {

    @NotBlank(message = "이메일이 입력되지 않았거나 공백입니다. ${validatedValue}")
    private String email;
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#]).{8,}$",
        message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.")
    private String password;
    @NotBlank(message = "코드가 입력되지 않았거나 공백입니다. ${validatedValue}")
    private String code;
    @NotBlank(message = "닉네임이 입력되지 않았거나 공백입니다. ${validatedValue}")
    private String nickname;
    @Size(min = 4, max = 4, message = "입력 연도의 형식이 잘못되었습니다. ${validatedValue}")
    private String year;
    @Size(min = 1, max = 2, message = "입력 달의 형식이 잘못되었습니다. ${validatedValue}")
    private String month;
    @Size(min = 1, max = 2, message = "입력 일의 형식이 잘못되었습니다. ${validatedValue}")
    private String day;
    @Max(value = 8, message = "지정할 수 있는 관심사의 범위를 초과하였습니다. ${validatedValue}")
    private Integer style1;
    @Max(value = 8, message = "지정할 수 있는 관심사의 범위를 초과하였습니다. ${validatedValue}")
    private Integer style2;
    @Max(value = 8, message = "지정할 수 있는 관심사의 범위를 초과하였습니다. ${validatedValue}")
    private Integer style3;
}
