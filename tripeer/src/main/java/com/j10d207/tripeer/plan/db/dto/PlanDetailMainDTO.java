package com.j10d207.tripeer.plan.db.dto;

import com.j10d207.tripeer.plan.db.entity.PlanEntity;
import com.j10d207.tripeer.plan.db.entity.PlanTownEntity;
import com.j10d207.tripeer.plan.db.vo.PlanCreateInfoVO;
import com.j10d207.tripeer.user.db.dto.UserDTO;
import com.j10d207.tripeer.user.db.entity.CoworkerEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
public class PlanDetailMainDTO {

    @Getter
    @Setter
    @Builder
    public static class CreateResultInfo {
        private long planId;
        private String title;
        private List<TownDTO> townList;
        private String vehicle;
        private LocalDate startDay;
        private LocalDate endDay;
        private LocalDate createDay;

        public static CreateResultInfo VOToDTO (PlanCreateInfoVO createInfo) {

            return CreateResultInfo.builder()
                    .title(createInfo.getTitle())
                    .townList(createInfo.getTownList())
                    .vehicle(createInfo.getVehicle())
                    .startDay(createInfo.getStartDay())
                    .endDay(createInfo.getEndDay())
                    .createDay(LocalDate.now(ZoneId.of("Asia/Seoul")))
                    .build();
        }
    }

    @Getter
    @Builder
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

        public static MyPlan EntityToDTO (PlanEntity plan, String img, List<PlanTownEntity> planTown, List<CoworkerEntity> memberList) {
            return MyPlan.builder()
                    .planId(plan.getPlanId())
                    .title(plan.getTitle())
                    .img(img)
                    .townList(PlanTownEntity.ConvertToNameList(planTown))
                    .startDay(plan.getStartDate())
                    .endDay(plan.getEndDate())
                    .member(memberList.stream().map(UserDTO.Search::CoworkerEntityToDTO).toList())
                    .newPlan((int) ChronoUnit.DAYS.between(plan.getCreateDate(), LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1)) < 3)
                    .build();
        }
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

}