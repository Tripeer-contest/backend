package com.j10d207.tripeer.plan.dto.res;

import com.j10d207.tripeer.plan.dto.req.PlaceListReq;
import com.j10d207.tripeer.tmap.db.dto.PublicRootDTO;
import lombok.*;
import org.springframework.beans.BeanUtils;

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
public class RootOptimizeDTO {

    private int option;
    private List<String[]> spotTime;
    private List<place> placeList;
    private List<PublicRootDTO> publicRootList;

    @Getter
    @Setter
    @ToString
    public static class place {

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
    }

    public static RootOptimizeDTO ofPlaceListReq(PlaceListReq placeListReq) {
        return RootOptimizeDTO.builder()
                .option(placeListReq.getOption())
                .placeList(placeListReq.getPlaceList().stream().map(place -> {
                    RootOptimizeDTO.place newPlaceList = new place();
                    BeanUtils.copyProperties(place, newPlaceList);
                    return newPlaceList;
                }).toList())
                .build();
    }



}
