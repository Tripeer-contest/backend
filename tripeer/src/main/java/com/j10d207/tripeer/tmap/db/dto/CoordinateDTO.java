package com.j10d207.tripeer.tmap.db.dto;

import com.j10d207.tripeer.plan.dto.res.RootOptimizeDTO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
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
