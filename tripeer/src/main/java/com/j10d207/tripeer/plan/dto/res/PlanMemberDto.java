package com.j10d207.tripeer.plan.dto.res;

import com.j10d207.tripeer.plan.db.entity.PlanEntity;
import com.j10d207.tripeer.plan.db.entity.PlanTownEntity;
import com.j10d207.tripeer.user.db.entity.CoworkerEntity;
import com.j10d207.tripeer.user.dto.res.UserDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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

        public static Pending ofCoworkerEntity(CoworkerEntity coworkerEntity, List<UserDTO.Search> memberList) {
            PlanEntity plan = coworkerEntity.getPlan();
            return PlanMemberDto.Pending.builder()
                    .inviteUser(UserDTO.Search.fromUserEntity(coworkerEntity.getInvite()))
                    .planId(plan.getPlanId())
                    .title(plan.getTitle())
                    .townList(PlanTownEntity.convertToNameList(plan.getPlanTown()))
                    .startDay(plan.getStartDate())
                    .endDay(plan.getEndDate())
                    .memberList(memberList)
                    .build();
        }

    }
}
