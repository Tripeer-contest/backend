package com.j10d207.tripeer.plan.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class PlaceListReq {

    @NotNull(message = "${${validatedValue} is null")
    private int option;
    @NotNull (message = "${${validatedValue} is null")
    private List<place> placeList;

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
}
