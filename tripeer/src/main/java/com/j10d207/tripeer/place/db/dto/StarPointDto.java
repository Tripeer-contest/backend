package com.j10d207.tripeer.place.db.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StarPointDto {

    private long spotStarPointId;
    private long userId;
    private String nickname;
    private String profileImage;

    private int spotInfoId;


    private double starPoint;
    private String message;
    private LocalDateTime createTime;
    private String image1;
    private String image2;
    private String image3;
    private String image4;

}
