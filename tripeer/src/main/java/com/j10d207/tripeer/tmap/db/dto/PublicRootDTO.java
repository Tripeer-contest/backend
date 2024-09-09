package com.j10d207.tripeer.tmap.db.dto;

import com.j10d207.tripeer.tmap.db.entity.PublicRootDetailEntity;
import com.j10d207.tripeer.tmap.db.entity.PublicRootEntity;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class PublicRootDTO {

    //총 소요 시간(min)
    private int totalTime;
    //총 거리(km)
    private double totalDistance;
    //총 도보 시간(min)
    private int totalWalkTime;
    //총 도보 거리
    private int totalWalkDistance;

    /* 경로 탐색 결과 종류
     * 1 - 지하철 SUBWAY
     * 2 - 버스 BUS
     * 3 - 버스 + 지하철 BUS AND SUBWAY
     * 4 - 고속/시외버스 EXPRESS BUS
     * 5 - 기차 TRAIN
     * 6 - 항공 AIRPLANE
     * 7 - 해운 FERRY
     */
    private int pathType;
    //경로 총 요금
    private int totalFare;

    private List<PublicRootDetail> publicRootDetailList;

    public static PublicRootDTO fromJson (JsonObject jsonObject) {
        JsonArray legs = jsonObject.getAsJsonArray("legs");

        List<PublicRootDTO.PublicRootDetail> detailList = new ArrayList<>();

        for (JsonElement leg : legs) {
            JsonObject legObject = leg.getAsJsonObject();
            detailList.add(PublicRootDTO.PublicRootDetail.fromJson(legObject));
        }

        return PublicRootDTO.builder()
                .totalDistance(jsonObject.get("totalDistance").getAsInt())
                .totalWalkTime(jsonObject.get("totalWalkTime").getAsInt())
                .totalWalkDistance(jsonObject.get("totalWalkDistance").getAsInt())
                .pathType(jsonObject.get("pathType").getAsInt())
                .totalFare(jsonObject.getAsJsonObject("fare").getAsJsonObject("regular").get("totalFare").getAsInt())
                .publicRootDetailList(detailList)
                .build();
    }

    public static PublicRootDTO ofEntityAndDetailList (PublicRootEntity publicRoot, List<PublicRootDetailEntity> publicRootDetailEntityList) {
        List<PublicRootDTO.PublicRootDetail> detailList = new ArrayList<>();
        for (PublicRootDetailEntity publicRootDetailEntity : publicRootDetailEntityList) {
            detailList.add(PublicRootDTO.PublicRootDetail.fromEntity(publicRootDetailEntity));
        }

        return PublicRootDTO.builder()
                .totalDistance(publicRoot.getTotalDistance())
                .totalWalkTime(publicRoot.getTotalWalkTime())
                .totalWalkDistance(publicRoot.getTotalWalkDistance())
                .pathType(publicRoot.getPathType())
                .totalFare(publicRoot.getTotalFare())
                .totalTime(publicRoot.getTotalTime())
                .publicRootDetailList(detailList)
                .build();
    }
    @Getter
    @Setter
    @Builder
    @ToString
    public static class PublicRootDetail {

        //구간 이동 거리 (m)
        private int distance;
        //구간 소요 시간
        private int sectionTime;
        /* 경로 탐색 결과 종류
         * 0 - 자동차(택시)
         * 1 - 도보 WALK
         * 2 - 버스 BUS
         * 3 - 지하철 SUBWAY
         * 4 - 고속/시외버스 EXPRESS BUS
         * 5 - 기차 TRAIN
         * 6 - 항공 AIRPLANE
         * 7 - 해운 FERRY
         */
        private String mode;
        private String route;

        //시작 지점 정보
        private String startName;
        private double startLat;
        private double startLon;

        //구간 도착 지점 정보
        private String endName;
        private double endLat;
        private double endLon;

        public static PublicRootDetail fromEntity(PublicRootDetailEntity publicRootDetail) {
            return PublicRootDetail.builder()
                    .startName(publicRootDetail.getStartName())
                    .startLat(publicRootDetail.getStartLat())
                    .startLon(publicRootDetail.getStartLon())
                    .endName(publicRootDetail.getEndName())
                    .endLat(publicRootDetail.getEndLat())
                    .endLon(publicRootDetail.getEndLon())
                    .distance(publicRootDetail.getDistance())
                    .sectionTime(publicRootDetail.getSectionTime())
                    .mode(publicRootDetail.getMode())
                    .route(publicRootDetail.getRoute())
                    .build();
        }

        public static PublicRootDetail fromJson(JsonObject jsonObject) {

            return PublicRootDetail.builder()
                    //구간 이동 거리 (m)
                    .distance(jsonObject.get("distance").getAsInt())
                    //구간 소요 시간
                    .sectionTime(jsonObject.get("sectionTime").getAsInt()/60)
                    /* 경로 탐색 결과 종류
                     * 0 - 자동차(택시)
                     * 1 - 도보 WALK
                     * 2 - 버스 BUS
                     * 3 - 지하철 SUBWAY
                     * 4 - 고속/시외버스 EXPRESS BUS
                     * 5 - 기차 TRAIN
                     * 6 - 항공 AIRPLANE
                     * 7 - 해운 FERRY
                     */
                    .mode(jsonObject.get("mode").getAsString())
                    //대중교통 노선 명칭
                    .route(jsonObject.has("route") ? jsonObject.get("route").getAsString() : null)
                    //시작 지점 정보
                    .startName(jsonObject.getAsJsonObject("start").get("name").getAsString())
                    .startLat(jsonObject.getAsJsonObject("start").get("lat").getAsDouble())
                    .startLon(jsonObject.getAsJsonObject("start").get("lon").getAsDouble())
                    //구간 도착 지점 정보
                    .endName(jsonObject.getAsJsonObject("end").get("name").getAsString())
                    .endLat(jsonObject.getAsJsonObject("end").get("lat").getAsDouble())
                    .endLon(jsonObject.getAsJsonObject("end").get("lon").getAsDouble())
                    .build();
        }

    }


}
