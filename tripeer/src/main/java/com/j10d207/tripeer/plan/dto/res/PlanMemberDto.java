package com.j10d207.tripeer.plan.dto.res;

import com.j10d207.tripeer.user.dto.res.UserDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class PlanMemberDto {

    @Getter
    @Builder
    public static class Pending {

        private UserDTO.Search inviteUser;

        private long planId;
        private String title;
        private List<String> townList;
        private LocalDate startDay;
        private LocalDate endDay;

        private List<UserDTO.Search> memberList;

    }
}
