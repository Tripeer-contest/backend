package com.j10d207.tripeer.plan.dto.res;

import com.j10d207.tripeer.plan.dto.req.PlaceListReq;
import com.j10d207.tripeer.tmap.db.dto.PublicRootDTO;
import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
/*
Response 의 역할을 하고있음, 이동 방법을 입력해서 Res 하거나 VO를 통해 받은 목적지 순서를 재배치해서 Res 해줌
 */
public class RootRes {

    private int option;
    private List<String[]> spotTime;
    private List<Place> placeList;
    private List<PublicRootDTO> publicRootList;

    @Getter
    @Setter
    @Builder
    @ToString
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

    public static RootRes ofPlaceListReq(PlaceListReq placeListReq) {
        return RootRes.builder()
                .option(placeListReq.getOption())
                .placeList(placeListReq.getPlaceList().stream().map(Place::fromReq).toList())
                .build();
    }



}
