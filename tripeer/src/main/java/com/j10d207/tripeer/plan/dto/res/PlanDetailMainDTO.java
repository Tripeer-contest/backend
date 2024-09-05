package com.j10d207.tripeer.plan.dto.res;

import com.j10d207.tripeer.place.db.ContentTypeEnum;
import com.j10d207.tripeer.plan.db.dto.TownDTO;
import com.j10d207.tripeer.plan.db.entity.PlanDetailEntity;
import com.j10d207.tripeer.plan.db.entity.PlanEntity;
import com.j10d207.tripeer.plan.db.entity.PlanTownEntity;
import com.j10d207.tripeer.plan.dto.req.PlanCreateInfoReq;
import com.j10d207.tripeer.user.dto.res.UserDTO;
import com.j10d207.tripeer.user.db.entity.CoworkerEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

/*
하나의 여행 일정 단위에 사용되는 데이터들의 DTO 모음
 */
@Getter
public class PlanDetailMainDTO {

    /*
    플랜 생성 결과를 반환하기 위한 DTO
     */
    @Getter
    @Setter
    @Builder
    public static class CreateResultInfo {
        private long planId;
        private String title;
        private List<TownDTO> townList;
        private Boolean isSaved;
        private LocalDate startDay;
        private LocalDate endDay;
        private LocalDate createDay;

        public static CreateResultInfo fromPlanCreateInfoReq (PlanCreateInfoReq createInfo) {

            return CreateResultInfo.builder()
                    .title(createInfo.getTitle())
                    .townList(createInfo.getTownList())
                    .isSaved(createInfo.getIsSaved())
                    .startDay(createInfo.getStartDay())
                    .endDay(createInfo.getEndDay())
                    .createDay(LocalDate.now(ZoneId.of("Asia/Seoul")))
                    .build();
        }
    }

    /*
    내가 가진 여행 계획 1개단위의 정보를 Response 해주기위한 DTO, 여행계획은 여러개 일 수 있으므로 주로 List 반환됨
     */
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

        public static MyPlan valueOfPlanPlanTownCoworkerEntity(PlanEntity plan, String img, List<PlanTownEntity> planTown, List<CoworkerEntity> memberList) {
            return MyPlan.builder()
                    .planId(plan.getPlanId())
                    .title(plan.getTitle())
                    .img(img)
                    .townList(PlanTownEntity.convertToNameList(planTown))
                    .startDay(plan.getStartDate())
                    .endDay(plan.getEndDate())
                    .member(memberList.stream().map(UserDTO.Search::fromCoworkerEntity).toList())
                    .newPlan((int) ChronoUnit.DAYS.between(plan.getCreateDate(), LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1)) < 3)
                    .build();
        }
    }

    /*
    여행 계획에 접속했을때 첫 페이지에 표시될 정보들이 Response 될 DTO
     */
    @Getter
    @AllArgsConstructor
    @Builder
    public static class MainPageInfo {
        private long planId;
        private String title;
        private List<TownDTO> townList;
        private List<UserDTO.Search> coworkerList;
    }

    /*
    여행 계획에 포함된 동행자들의 data 를 담을때 사용하는 DTO
     */
    @Getter
    @Builder
    public static class PlanCoworker {
        private int order;
        private long planId;
        private long userId;
        private String profileImage;
        private String nickname;

        public static PlanCoworker fromCoworkerEntity (CoworkerEntity coworkerEntity, int order) {
            return PlanCoworker.builder()
                    .order(order)
                    .planId(coworkerEntity.getPlan().getPlanId())
                    .userId(coworkerEntity.getUser().getUserId())
                    .nickname(coworkerEntity.getUser().getNickname())
                    .profileImage(coworkerEntity.getUser().getProfileImage())
                    .build();
        }
    }
    
    /*
    여행 일자에 포함된 여행 장소의 정보를 담는 DTO
    일자, 방문순번, 다음 장소로 이동하는 시간 등이 포함된다.
     */
    @Getter
    @Builder
    public static class PlanSpotDetail {
        private long planDetailId;
        private String title;
        private String contentType;
        private int day;
        private int step;
        private LocalTime spotTime;
        private String description;
        private String movingRoot;
        private int cost;

        public static PlanSpotDetail fromEntity(PlanDetailEntity planDetailEntity) {
                return PlanSpotDetail.builder()
                        .planDetailId(planDetailEntity.getPlanDetailId())
                        .title(planDetailEntity.getSpotInfo().getTitle())
                        .contentType(ContentTypeEnum.getNameByCode(planDetailEntity.getSpotInfo().getContentTypeId()))
                        .day(planDetailEntity.getDay())
                        .step(planDetailEntity.getStep())
                        .spotTime(planDetailEntity.getSpotTime())
                        .description(planDetailEntity.getDescription())
                        .cost(planDetailEntity.getCost())
                        .build();

        }
    }

}
