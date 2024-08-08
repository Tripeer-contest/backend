package com.j10d207.tripeer.place.db.dto;


import com.j10d207.tripeer.place.db.ContentTypeEnum;
import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SpotAddResDto {
        private int spotInfoId;    // 장소 정보 ID
        private String title;       // 장소 제목
        private String contentType; // 컨텐츠 타입
        private String addr;        // 주소
        private double latitude;    // 위도
        private double longitude;   // 경도
        private String img;         // 이미지 URL
        private boolean spot;       // 스팟 여부
        private boolean wishlist;   // 위시리스트에 추가되었는지 여부

        public static SpotAddResDto EntityToDTO (SpotInfoEntity spotInfo, boolean isSpot) {

                return SpotAddResDto.builder()
                        .spotInfoId(spotInfo.getSpotInfoId())
                        .title(spotInfo.getTitle())
                        .contentType(ContentTypeEnum.getNameByCode(spotInfo.getContentTypeId()))
                        .addr(spotInfo.getAddr1())
                        .latitude(spotInfo.getLatitude())
                        .longitude(spotInfo.getLongitude())
                        .img(spotInfo.getFirstImage())
                        .spot(isSpot)
                        .wishlist(false)
                        .build();

        }
}
