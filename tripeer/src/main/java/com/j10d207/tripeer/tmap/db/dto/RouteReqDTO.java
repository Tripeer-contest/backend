package com.j10d207.tripeer.tmap.db.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
/*
restTemplate 요청을 위해 파라미터로 사용하기 위한 좌표저장 DTO
 */
public class RouteReqDTO {

    private String startX;
    private String startY;
    private String endX;
    private String endY;

    public static RouteReqDTO doubleConvertString(double SX, double SY, double EX, double EY) {
        return RouteReqDTO.builder()
                .startX(String.valueOf(SX))
                .startY(String.valueOf(SY))
                .endX(String.valueOf(EX))
                .endY(String.valueOf(EY))
                .build();
    }

}
