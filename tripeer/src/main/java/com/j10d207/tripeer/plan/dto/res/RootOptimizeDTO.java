package com.j10d207.tripeer.plan.dto.res;

import com.j10d207.tripeer.plan.db.dto.PublicRootDTO;
import com.j10d207.tripeer.plan.dto.req.PlaceListReq;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Getter
@Builder
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
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

    public static RootOptimizeDTO PlaceListVOTODTO(PlaceListReq placeListReq) {
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