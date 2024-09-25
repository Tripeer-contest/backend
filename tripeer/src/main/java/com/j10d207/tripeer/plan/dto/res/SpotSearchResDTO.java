package com.j10d207.tripeer.plan.dto.res;

import com.j10d207.tripeer.place.db.ContentTypeEnum;
import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import com.j10d207.tripeer.place.db.entity.SpotReviewEntity;
import com.j10d207.tripeer.user.db.entity.WishListEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder
@Setter
@AllArgsConstructor
public class SpotSearchResDTO {

    private List<SearchResult> searchResultList;
    private boolean isLastPage;

    @Getter
    @Builder
    public static class SearchResult {

        private long spotInfoId;
        private String title;
        private String contentType;
        private String addr;
        private double starPointAvg;
        private Double latitude;
        private Double longitude;
        private String img;
        private boolean isWishlist;
        private boolean isSpot;

        public static SearchResult fromWishListEntity(WishListEntity wishList, boolean isSpot) {

            return SearchResult.builder()
                    .spotInfoId(wishList.getSpotInfo().getSpotInfoId())
                    .title(wishList.getSpotInfo().getTitle())
                    .contentType(ContentTypeEnum.getNameByCode(wishList.getSpotInfo().getContentTypeId()))
                    .addr(wishList.getSpotInfo().getAddr1())
                    .starPointAvg(wishList.getSpotInfo().getStarPointAvg())
                    .latitude(wishList.getSpotInfo().getLatitude())
                    .longitude(wishList.getSpotInfo().getLongitude())
                    .img(wishList.getSpotInfo().getFirstImage())
                    .isWishlist(true)
                    .isSpot(isSpot)
                    .build();
        }

        public static SearchResult fromSpotInfoEntity(SpotInfoEntity spotInfo, boolean isWishlist, boolean isSpot) {
            String img;
            if (spotInfo.getFirstImage().contains("default")) {
                img = spotInfo.getFirstImage();
            } else {
                img = "https://tripeer207.s3.ap-northeast-2.amazonaws.com/spot/"+spotInfo.getSpotInfoId()+".png";
            }
            return SearchResult.builder()
                    .spotInfoId(spotInfo.getSpotInfoId())
                    .title(spotInfo.getTitle())
                    .contentType(ContentTypeEnum.getMajorNameByCode(spotInfo.getContentTypeId()))
                    .addr(spotInfo.getAddr1())
                    .starPointAvg(spotInfo.getStarPointAvg())
                    .latitude(spotInfo.getLatitude())
                    .longitude(spotInfo.getLongitude())
                    .img(img)
                    .isWishlist(isWishlist)
                    .isSpot(isSpot)
                    .build();
        }
    }



}
