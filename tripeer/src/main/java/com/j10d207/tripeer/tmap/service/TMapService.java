package com.j10d207.tripeer.tmap.service;

import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.j10d207.tripeer.plan.db.dto.PublicRootDTO;
import com.j10d207.tripeer.tmap.db.dto.CoordinateDTO;
import com.j10d207.tripeer.tmap.db.dto.RootInfoDTO;
import com.j10d207.tripeer.tmap.db.entity.PublicRootEntity;

import java.util.List;

public interface TMapService {

    //최단 경로 찾기
    public FindRoot getOptimizingTime(List<CoordinateDTO> coordinates);

    //경로별 시간 배열 만들기
    public RootInfoDTO[][] getTimeTable(List<CoordinateDTO> coordinates);

    // 경로 시간 받아오기
    public RootInfoDTO getPublicTime(double SX, double SY, double EX, double EY, RootInfoDTO timeRootInfoDTO);

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