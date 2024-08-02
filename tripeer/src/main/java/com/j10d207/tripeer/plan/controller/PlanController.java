package com.j10d207.tripeer.plan.controller;

import com.j10d207.tripeer.plan.db.dto.*;
import com.j10d207.tripeer.plan.db.vo.PlanCreateInfoVO;
import com.j10d207.tripeer.plan.db.vo.PlanDetailVO;
import com.j10d207.tripeer.plan.service.PlanService;
import com.j10d207.tripeer.response.Response;
import com.j10d207.tripeer.user.db.dto.CustomOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanController {

    private final PlanService planService;
    //플랜 생성
    @PostMapping
    public Response<PlanDetailMainDTO.CreateResultInfo> createPlan(@RequestBody PlanCreateInfoVO createInfo, @AuthenticationPrincipal CustomOAuth2User user) {
        PlanDetailMainDTO.CreateResultInfo result = planService.createPlan(createInfo, user.getUserId());
        return Response.of(HttpStatus.OK, "플랜 생성 완료", result);
    }

    //플랜 이름 변경
    @PatchMapping("/title")
    public Response<Boolean> changeTitle(@RequestBody PlanDetailMainDTO.TitleChange titleChangeDTO, @AuthenticationPrincipal CustomOAuth2User user) {
        planService.changeTitle(titleChangeDTO, user.getUserId());
        return Response.of(HttpStatus.OK, "플랜 이름 변경 완료", true);
    }

    //플랜 탈퇴
    @DeleteMapping("/{planId}")
    public Response<?> planOut(@PathVariable("planId") long planId, @AuthenticationPrincipal CustomOAuth2User user) {
        planService.planOut(planId, user.getUserId());
        return Response.of(HttpStatus.OK, "플랜 탈퇴 완료", null);
    }

    //내 플랜 리스트 조회
    @GetMapping
    public Response<List<PlanDetailMainDTO.MyPlan>> getPlanList(@AuthenticationPrincipal CustomOAuth2User user) {
        List<PlanDetailMainDTO.MyPlan> planList = planService.planList(user.getUserId());
        return Response.of(HttpStatus.OK, "내 플랜 리스트 조회", planList);
    }

    //플랜 디테일 메인 조회
    @GetMapping("/main/{planId}")
    public Response<PlanDetailMainDTO.MainPageInfo> getPlanDetailMain(@PathVariable("planId") long planId, @AuthenticationPrincipal CustomOAuth2User user) {
        PlanDetailMainDTO.MainPageInfo result = planService.getPlanDetailMain(planId, user.getUserId());
        return Response.of(HttpStatus.OK, "플랜 메인 조회", result);
    }

    //동행자 추가
    @PostMapping("/member")
    public Response<?> joinPlan(@RequestBody PlanDetailMainDTO.PlanCoworker planCoworker, @AuthenticationPrincipal CustomOAuth2User user) {
        planService.joinPlan(planCoworker, user.getUserId());
        return Response.of(HttpStatus.OK, "초대 완료", null);
    }

    //플랜에서 나의 정보 조회(기존 내정보 + 나의 coworker에서의 순서)
    @GetMapping("/myinfo/{planId}")
    public Response<PlanDetailMainDTO.PlanCoworker> getCoworker(@PathVariable("planId") long planId, @AuthenticationPrincipal CustomOAuth2User user) {
        PlanDetailMainDTO.PlanCoworker planCoworker = planService.getPlanMyinfo(planId, user.getUserId());
        return Response.of(HttpStatus.OK, "조회 완료", planCoworker);
    }

    //동행자 조회
    @GetMapping("/member/{planId}")
    public Response<List<PlanDetailMainDTO.PlanCoworker>> getCoworker(@PathVariable("planId") long planId) {
        List<PlanDetailMainDTO.PlanCoworker> planCoworkerList = planService.getCoworker(planId);
        return Response.of(HttpStatus.OK, "조회 완료", planCoworkerList);
    }

    //관광지 검색
    @GetMapping("/spot")
    public Response<List<SpotSearchResDTO>> getSpots(@RequestParam("planId") long planId, @RequestParam("keyword") String keyword, @RequestParam("page") int page, @RequestParam("sortType") int sortType, @AuthenticationPrincipal CustomOAuth2User user) {
        List<SpotSearchResDTO> searchResList = planService.getSpotSearch(planId, keyword, page, sortType, user.getUserId());
        return Response.of(HttpStatus.OK, "검색 완료", searchResList);
    }

    //플랜버킷 관광지 추가
    @PostMapping("/bucket")
    public Response<?> addPlanSpot(@RequestParam("planId") long planId, @RequestParam("spotInfoId") int spotInfoId, @AuthenticationPrincipal CustomOAuth2User user) {
        planService.addPlanSpot(planId, spotInfoId, user.getUserId());
        return Response.of(HttpStatus.OK, "플랜버킷 관광지 추가 완료", null);
    }

    //플랜버킷 관광지 삭제
    @DeleteMapping("/bucket")
    public Response<?> delPlanSpot(@RequestParam("planId") long planId, @RequestParam("spotInfoId") int spotInfoId, @AuthenticationPrincipal CustomOAuth2User user) {
        planService.delPlanSpot(planId, spotInfoId, user.getUserId());
        return Response.of(HttpStatus.OK, "플랜버킷 관광지 삭제 완료", null);
    }

    //즐겨찾기 추가
    @PostMapping("/wishlist/{spotInfoId}")
    public Response<?> addWishList(@PathVariable("spotInfoId") int spotInfoId, @AuthenticationPrincipal CustomOAuth2User user) {
        planService.addWishList(spotInfoId, user.getUserId());
        return Response.of(HttpStatus.OK, "즐겨찾기 추가 완료", null);
    }

    //즐겨찾기 조회
    @GetMapping("wishlist/{planId}")
    public Response<List<SpotSearchResDTO>> getWishList(@AuthenticationPrincipal CustomOAuth2User user, @PathVariable("planId") long planId) {
        List<SpotSearchResDTO> searchResDTOList = planService.getWishList(user.getUserId(), planId);
        return Response.of(HttpStatus.OK, "즐겨찾기 리스트 조회 완료", searchResDTOList);
    }

    //플랜 디테일 저장
    @PostMapping("/detail")
    public Response<?> addPlanDetail(@RequestBody PlanDetailVO planDetailVO) {
        planService.addPlanDetail(planDetailVO);
        return Response.of(HttpStatus.OK, "플랜 디테일 저장 완료", null);
    }

    //플랜 디테일 전체 조회
    @GetMapping("/detail/{planId}")
    public Response<Map<Integer, List<PlanDetailResDTO>>> getPlan(@PathVariable("planId") long planId) {
        Map<Integer, List<PlanDetailResDTO>> result = planService.getAllPlanDetail(planId);
        return Response.of(HttpStatus.OK, "플랜 디테일 전체 조회 완료", result);
    }

    //목적지간 최단 루트 계산
    @PostMapping("/optimizing/short")
    public Response<RootOptimizeDTO> getShortTime(@RequestBody RootOptimizeDTO rootOptimizeDTO) {
        return Response.of(HttpStatus.OK, "목적지 간 대중교통 경로, 자차 소요시간 조회.", planService.getShortTime(rootOptimizeDTO));
    }

    //플랜 최단거리 조정
    @PostMapping("/optimizing")
    public Response<RootOptimizeDTO> getOptimizedPlan(@RequestBody RootOptimizeDTO rootOptimizeReqDTO) throws IOException {
        RootOptimizeDTO result = planService.getOptimizingTime(rootOptimizeReqDTO);
        return Response.of(HttpStatus.OK, "목적지 리스트 최적화 완료", result);
    }
}