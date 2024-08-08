package com.j10d207.tripeer.tmap.db.dto;

import com.j10d207.tripeer.plan.db.dto.RootOptimizeDTO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
/*
최적화시 목적지들의 좌표 리스트를 담기 위한 DTO
 */
public class CoordinateDTO {

    private double latitude;
    private double longitude;
    private String title;

    public static CoordinateDTO PlaceToCoordinate (RootOptimizeDTO.place place) {
        return CoordinateDTO.builder()
                .title(place.getTitle())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .build();
    }

}
