package com.j10d207.tripeer.user.dto.req;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PasswordChangeReq {
	private String email;
	@Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#]).{8,}$",
		message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.")
	private String password;
	private String code;
}
