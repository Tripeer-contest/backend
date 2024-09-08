package com.j10d207.tripeer.place.db.dto;

import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SpotInfoDto {

    private int spotId;
    private String spotName;
    private String spotImg;
    private String address;
    private double latitude;
    private double longitude;
    private boolean isWishlist;

    public static SpotInfoDto convertToDto(SpotInfoEntity spotInfoEntity, boolean isWishlist) {

        return SpotInfoDto.builder()
                .spotId(spotInfoEntity.getSpotInfoId())
                .latitude(spotInfoEntity.getLatitude())
                .spotImg(spotInfoEntity.getFirstImage())
                .longitude(spotInfoEntity.getLongitude())
                .address(spotInfoEntity.getAddr1())
                .spotName(spotInfoEntity.getTitle())
                .isWishlist(isWishlist)
                .build();
    }
}
