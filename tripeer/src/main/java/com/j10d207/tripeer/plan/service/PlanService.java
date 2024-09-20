package com.j10d207.tripeer.plan.service;

import com.j10d207.tripeer.plan.dto.req.*;
import com.j10d207.tripeer.plan.dto.res.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PlanService {

    //플랜 생성
    public PlanDetailMainDTO.CreateResultInfo createPlan(PlanCreateInfoReq planCreateInfoReq, long userId);

    //플랜 이름 변경
    public void changeTitle(TitleChangeReq titleChangeReq, long userId);

    //플랜 탈퇴
    public void planOut(long planId, long userId);

    //내 플랜 리스트 조회
    public List<PlanDetailMainDTO.MyPlan> planList(long userId);

    //플랜 디테일 메인 조회
    public PlanDetailMainDTO.MainPageInfo getPlanDetailMain(long planId, long userId);

    //동행자 초대
    public void invitePlan(CoworkerInvitedReq coworkerInvitedReq, long userId);

    //동행자 추가
    public void joinPlan(long planId, long userId);

    //초대된 플랜 리스트 조회하기
    public List<PlanMemberDto.Pending> getPendingList(long userId);

    //동행자 조회
    public List<PlanDetailMainDTO.PlanCoworker> getCoworker(long planId);

    //관광지 검색
    public SpotSearchResDTO getSpotSearch(long planId, String keyword, int page, int sortType, long userId, int cityId, int townId);

    //플랜버킷 관광지 추가
    public void addPlanSpot(long planId, int spotInfoId, long userId);

    //플랜버킷 관광지 삭제
    public void delPlanSpot(long planId, int spotInfoId, long userId);

    //즐겨찾기 조회
    public List<SpotSearchResDTO.SearchResult> getWishList(long userId, long planId);

    //플린 디테일 저장
    public void addPlanDetail(PlanDetailReq planDetailReq);

    //플랜 디테일 전체 조회
    public Map<Integer, List<PlanDetailMainDTO.PlanSpotDetail>> getAllPlanDetail(long planId);

    //플랜 나의 정보 조회(기존 내정보 + 나의 coworker에서의 순서)
    public PlanDetailMainDTO.PlanCoworker getPlanMyInfo(long planId, long userId);

    //목적지간 최단 루트 계산
    public RootRes getShortTime(RootRes rootRes);

    //목적지간 최단 루트 계산
    public AtoBRes getShortTime2(PlaceListReq placeListReq);

    //플랜 최단거리 조정
    public RootRes getOptimizingTime(RootRes rootOptimizeReqDTO) throws IOException;

    //플랜 최단거리 조정
    public OptimizingRes getOptimizingTime2(PlaceListReq placeListReq) throws IOException;

    //지도 줌레벨 기반의 관광지 검색
    SpotSearchResDTO getSpotsInMap(long planId, String keyword, int page, double minLat, double maxLat,
                                         double minLon, double maxLon, int sortType, long userId);
}
