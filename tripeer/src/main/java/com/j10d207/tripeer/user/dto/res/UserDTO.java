package com.j10d207.tripeer.user.dto.res;

import com.j10d207.tripeer.place.db.ContentTypeEnum;
import com.j10d207.tripeer.user.db.entity.CoworkerEntity;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import com.j10d207.tripeer.user.db.entity.WishListEntity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;

@Getter
public class UserDTO {

    @Getter
    @Builder
    public static class Info {
        private long userId;
        private String email;
        private String nickname;
        private LocalDate birth;
        private String profileImage;
        private String style1;
        private int style1Num;
        private String style2;
        private int style2Num;
        private String style3;
        private int style3Num;

        public static Info fromUserEntity(UserEntity userEntity) {
            return UserDTO.Info.builder()
                    .userId(userEntity.getUserId())
                    .nickname(userEntity.getNickname())
                    .email(userEntity.getEmail())
                    .birth(userEntity.getBirth())
                    .profileImage(userEntity.getProfileImage())
                    .style1(userEntity.getStyle1())
                    .style2(userEntity.getStyle2())
                    .style3(userEntity.getStyle3())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Wishlist {
        private long wishlistId;
        private long cityId;
        private long townId;
        private long spotInfoId;
        private String title;
        private String contentType;
        private String addr;
        private Double latitude;
        private Double longitude;
        private String img;

        public static Wishlist fromEntity(WishListEntity wishListEntity) {
            return Wishlist.builder()
                    .wishlistId(wishListEntity.getWishlistId())
                    .cityId(wishListEntity.getSpotInfo().getTown().getTownPK().getCity().getCityId())
                    .townId(wishListEntity.getSpotInfo().getTown().getTownPK().getTownId())
                    .spotInfoId(wishListEntity.getSpotInfo().getSpotInfoId())
                    .title(wishListEntity.getSpotInfo().getTitle())
                    .contentType(ContentTypeEnum.getMajorNameByCode(wishListEntity.getSpotInfo().getContentTypeId()))
                    .addr(wishListEntity.getSpotInfo().getAddr1())
                    .latitude(wishListEntity.getSpotInfo().getLatitude())
                    .longitude(wishListEntity.getSpotInfo().getLongitude())
                    .img(wishListEntity.getSpotInfo().getFirstImage())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Search {
        private long userId;
        private String nickname;
        private String profileImage;

        public static Search fromUserEntity(UserEntity userEntity) {
            return Search.builder()
                    .userId(userEntity.getUserId())
                    .nickname(userEntity.getNickname())
                    .profileImage(userEntity.getProfileImage())
                    .build();
        }

        public static Search fromCoworkerEntity(CoworkerEntity coworkerEntity) {
                 return Search.builder()
                        .userId(coworkerEntity.getUser().getUserId())
                        .nickname(coworkerEntity.getUser().getNickname())
                        .profileImage(coworkerEntity.getUser().getProfileImage())
                        .build();
        }
    }

    @Getter
    @Builder
    public static class Social {
        private String nickname;
        private String birth;
        private String profileImage;

        public static Social getContext() {
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = context.getAuthentication();
            CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

            return UserDTO.Social.builder()
                    .nickname(customUserDetails.getName())
                    .profileImage(customUserDetails.getProfileImage())
                    .build();
        }
    }
}
