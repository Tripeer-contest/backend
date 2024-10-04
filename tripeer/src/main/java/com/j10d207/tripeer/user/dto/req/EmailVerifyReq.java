package com.j10d207.tripeer.user.dto.req;

import lombok.Getter;

@Getter
public class EmailVerifyReq {
	private String email;
	private String code;
}
