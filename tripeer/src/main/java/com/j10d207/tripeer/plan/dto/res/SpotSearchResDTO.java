package com.j10d207.tripeer.plan.dto.res;

import com.j10d207.tripeer.place.db.ContentTypeEnum;
import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import com.j10d207.tripeer.user.db.entity.WishListEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class SpotSearchResDTO {

    private long spotInfoId;
    private String title;
    private String contentType;
    private String addr;
    private Double latitude;
    private Double longitude;
    private String img;
    private boolean isWishlist;
    private boolean isSpot;

    public static SpotSearchResDTO WishEntityToDTO (WishListEntity wishList, boolean isSpot) {

        return SpotSearchResDTO.builder()
                .spotInfoId(wishList.getSpotInfo().getSpotInfoId())
                .title(wishList.getSpotInfo().getTitle())
                .contentType(ContentTypeEnum.getNameByCode(wishList.getSpotInfo().getContentTypeId()))
                .addr(wishList.getSpotInfo().getAddr1())
                .latitude(wishList.getSpotInfo().getLatitude())
                .longitude(wishList.getSpotInfo().getLongitude())
                .img(wishList.getSpotInfo().getFirstImage())
                .isWishlist(true)
                .isSpot(isSpot)
                .build();
    }

    public static SpotSearchResDTO SpotInfoEntityToDTO (SpotInfoEntity spotInfo, boolean isWishlist, boolean isSpot) {
        String img;
        if (spotInfo.getFirstImage().contains("default")) {
            img = spotInfo.getFirstImage();
        } else {
            img = "https://tripeer207.s3.ap-northeast-2.amazonaws.com/spot/"+spotInfo.getSpotInfoId()+".png";
        }
        return SpotSearchResDTO.builder()
                .spotInfoId(spotInfo.getSpotInfoId())
                .title(spotInfo.getTitle())
                .contentType(ContentTypeEnum.getNameByCode(spotInfo.getContentTypeId()))
                .addr(spotInfo.getAddr1())
                .latitude(spotInfo.getLatitude())
                .longitude(spotInfo.getLongitude())
                .img(img)
                .isWishlist(isWishlist)
                .isSpot(isSpot)
                .build();
    }

}
