package com.j10d207.tripeer.place.dto.res;

import com.j10d207.tripeer.place.db.ContentTypeEnum;
import com.j10d207.tripeer.place.db.entity.SpotDescriptionEntity;
import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import com.j10d207.tripeer.place.db.entity.SpotReviewEntity;

import com.j10d207.tripeer.user.db.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class SpotDTO {

    @Getter
    @Builder
    public static class SpotAddResDTO {

        private String nickname;
        private String profileImage;
        private long userId;
        private int order;

        private int spotInfoId;    // 장소 정보 ID
        private String title;       // 장소 제목
        private String contentType; // 컨텐츠 타입
        private String addr;        // 주소
        private double latitude;    // 위도
        private double longitude;   // 경도
        private String img;         // 이미지 URL
        private boolean wishlist;   // 위시리스트에 추가되었는지 여부

        public static SpotAddResDTO ofEntity (SpotInfoEntity spotInfo, UserEntity userEntity, int order) {

            return SpotAddResDTO.builder()
                    .userId(userEntity.getUserId())
                    .profileImage(userEntity.getProfileImage())
                    .nickname(userEntity.getNickname())
                    .order(order)
                    .spotInfoId(spotInfo.getSpotInfoId())
                    .title(spotInfo.getTitle())
                    .contentType(ContentTypeEnum.getNameByCode(spotInfo.getContentTypeId()))
                    .addr(spotInfo.getAddr1())
                    .latitude(spotInfo.getLatitude())
                    .longitude(spotInfo.getLongitude())
                    .img(spotInfo.getFirstImage())
                    .wishlist(false)
                    .build();

        }
    }

    @Getter
    @Builder
    public static class SpotDetailDTO {
        private Double latitude;
        private Double longitude;
        private String spotImg;
        private String spotName;
        private String description;
        private boolean isWishList;

        public static SpotDetailDTO convertToDto(SpotDescriptionEntity spotDescriptionEntity) {

            SpotInfoEntity spotInfo = spotDescriptionEntity.getSpotInfo();

            return SpotDetailDTO.builder()
                    .latitude(spotInfo.getLatitude())
                    .longitude(spotInfo.getLongitude())
                    .spotImg(spotInfo.getFirstImage())
                    .spotName(spotInfo.getTitle())
                    .isWishList(false)
                    .description(spotDescriptionEntity.getOverview())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class SpotInfoDTO {
        private int spotId;
        private String spotName;
        private String spotImg;
        private String address;
        private double starPointAvg;
        private double latitude;
        private double longitude;
        private boolean isWishlist;

        public static SpotInfoDTO convertToDto(SpotInfoEntity spotInfoEntity, boolean isWishlist) {

            return SpotInfoDTO.builder()
                    .spotId(spotInfoEntity.getSpotInfoId())
                    .spotImg(spotInfoEntity.getFirstImage())
                    .address(spotInfoEntity.getAddr1())
                    .spotName(spotInfoEntity.getTitle())
                    .starPointAvg(spotInfoEntity.getSpotReviewList().stream().mapToDouble(
						SpotReviewEntity::getStarPoint).average().orElse(0.0))
                    .latitude(spotInfoEntity.getLatitude())
                    .longitude(spotInfoEntity.getLongitude())
                    .isWishlist(isWishlist)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class SpotListDTO {

        private boolean last;
        private List<SpotInfoDTO> spotInfoDTOList;
    }
}
