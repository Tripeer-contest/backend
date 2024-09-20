package com.j10d207.tripeer.plan.dto.res;

import com.j10d207.tripeer.plan.dto.req.PlaceListReq;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OptimzingRes {

    private List<Place> placeList;
    private List<AtoBRes> optimizing;

    @Getter
    @Builder
    public static class Place {

        private String addr;
        private String contentType;
        private String img;
        private double latitude;
        private double longitude;
        private String nickname;
        private int order;
        private long planId;
        private String profileImage;
        private boolean spot;
        private int spotInfoId;
        private String title;
        private int userId;
        private boolean wishlist;

        private String movingRoot;

        public static Place fromReq(PlaceListReq.PlaceReq placeReq) {
            return Place.builder()
                    .addr(placeReq.getAddr())
                    .contentType(placeReq.getContentType())
                    .img(placeReq.getImg())
                    .latitude(placeReq.getLatitude())
                    .longitude(placeReq.getLongitude())
                    .nickname(placeReq.getNickname())
                    .order(placeReq.getOrder())
                    .planId(placeReq.getPlanId())
                    .profileImage(placeReq.getProfileImage())
                    .spot(placeReq.isSpot())
                    .spotInfoId(placeReq.getSpotInfoId())
                    .title(placeReq.getTitle())
                    .userId(placeReq.getUserId())
                    .wishlist(placeReq.isWishlist())
                    .build();
        }
    }
}
