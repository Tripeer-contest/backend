package com.j10d207.tripeer.plan.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CoworkerInvitedReq {

    @NotNull(message = "초대하려는 계획이 전달되지 않았습니다.")
    private long planId;
    @NotNull(message = "초대하려는 대상이 전달되지 않았습니다.")
    private long userId;
}
