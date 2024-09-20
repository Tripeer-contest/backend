package com.j10d207.tripeer.tmap.db.dto;

import com.j10d207.tripeer.tmap.db.TmapErrorCode;
import com.nimbusds.jose.shaded.gson.JsonElement;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
/*
tmapAPI 요청 결과를 저장하기 위한 DTO
 */
public class RootInfoDTO {

    private int time;
    private String startTitle;
    private double startLatitude;
    private double startLongitude;
    private String endTitle;
    private double endLatitude;
    private double endLongitude;
    private JsonElement rootInfo;
    private PublicRootDTO publicRoot;

//    private int status;
    private TmapErrorCode status;

    public String timeToString () {
        StringBuilder timeString = new StringBuilder();
        if(this.time/60 > 0) {
            timeString.append(this.time/60).append("시간 ");
        }
        timeString.append(this.time%60).append("분");

        return timeString.toString();
    }

    public void setLocation(double SX, double SY, double EX, double EY) {
        this.startLatitude = SX;
        this.startLongitude = SY;
        this.endLatitude = EX;
        this.endLongitude = EY;
    }

    public static RootInfoDTO createOfLocation(double SX, double SY, double EX, double EY) {
        return RootInfoDTO.builder()
                .startLatitude(SX)
                .startLongitude(SY)
                .endLatitude(EX)
                .endLongitude(EY)
                .build();
    }

}
