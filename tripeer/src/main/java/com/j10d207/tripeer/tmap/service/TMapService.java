package com.j10d207.tripeer.tmap.service;


import com.j10d207.tripeer.plan.dto.req.PlaceListReq;
import com.j10d207.tripeer.plan.dto.res.RootRes;
import com.j10d207.tripeer.tmap.db.dto.CoordinateDTO;
import com.j10d207.tripeer.tmap.db.dto.RootInfoDTO;

import java.util.List;

public interface TMapService {

    //최단 경로 찾기
    public FindRoot getOptimizingTime(List<CoordinateDTO> coordinates, int option);

    // 경로 시간 받아오기
    public RootInfoDTO getPublicTime(double SX, double SY, double EX, double EY, RootInfoDTO timeRootInfoDTO);

    // 경로 시간 받아오기
    public RootInfoDTO getPublicTime2(double SX, double SY, double EX, double EY, RootInfoDTO timeRootInfoDTO);

    // 경로 시간 받아오기
    public List<RootInfoDTO> getPublicTime3(double SX, double SY, double EX, double EY, RootInfoDTO timeRootInfoDTO);


    //tMap 대중교통 경로 찾기 시작
    public List<RootInfoDTO> useTMapPublic3 (PlaceListReq placeListReq);

//    //경로 리스트 중에서 제일 좋은 경로 하나를 선정해서 반환 ( 시간 우선 )
//    public JsonElement getBestTime(JsonArray itineraries);
//
//    //최초에 조회된 경로를 저장
//    public void saveRootInfo(JsonElement rootInfo, double SX, double SY, double EX, double EY, int time);
//
//    // 경로 조회하기
//    public JsonObject getResult(double SX, double SY, double EX, double EY);
//
//    //DTO 만들기
//    public PublicRootDTO getRootDTO (PublicRootEntity publicRootEntity);
}
