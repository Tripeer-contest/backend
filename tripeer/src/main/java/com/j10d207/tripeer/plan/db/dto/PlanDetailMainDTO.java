package com.j10d207.tripeer.plan.db.dto;

import com.j10d207.tripeer.user.db.dto.UserDTO;
import com.j10d207.tripeer.user.db.dto.UserSearchDTO;
import com.j10d207.tripeer.user.db.entity.CoworkerEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class PlanDetailMainDTO {

    @Getter
    public static class CreateInfo {
        private String title;
        private List<TownDTO> townList;
        private String vehicle;
        private LocalDate startDay;
        private LocalDate endDay;
    }

    @Getter
    @Setter
    public static class CreateResultInfo {
        private long planId;
        private String title;
        private List<TownDTO> townList;
        private String vehicle;
        private LocalDate startDay;
        private LocalDate endDay;
        private LocalDate createDay;
    }

    @Getter
    @Setter
    public static class MyPlan {
        private long planId;
        private String title;
        private String img;
        private List<String> townList;
        private LocalDate startDay;
        private LocalDate endDay;
        private List<UserDTO.Search> member;
        private boolean newPlan;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class MainPageInfo {
        private long planId;
        private String title;
        private List<TownDTO> townList;
        private List<UserDTO.Search> coworkerList;
    }

    @Getter
    @Builder
    public static class PlanCoworker {
        private int order;
        private long planId;
        private long userId;
        private String profileImage;
        private String nickname;

        public static PlanCoworker CoworkerToDTO (CoworkerEntity coworkerEntity, int order) {
            return PlanCoworker.builder()
                    .order(order)
                    .planId(coworkerEntity.getPlan().getPlanId())
                    .userId(coworkerEntity.getUser().getUserId())
                    .nickname(coworkerEntity.getUser().getNickname())
                    .profileImage(coworkerEntity.getUser().getProfileImage())
                    .build();
        }
    }

    @Getter
    public static class TitleChange {
        private long planId;
        private String title;
    }
}