package com.j10d207.tripeer.user.dto.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class NotiReq {
	private boolean allowNotifications;
}
