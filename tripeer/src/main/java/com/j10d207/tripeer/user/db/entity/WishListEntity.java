package com.j10d207.tripeer.user.db.entity;

import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity(name = "wishlist")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishListEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // PK
    private long wishlistId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "SPOT_INFO_ID")
    private SpotInfoEntity spotInfo;

    public static WishListEntity MakeWishListEntity (int spotInfoId, long userId) {
        return WishListEntity.builder()
                .user(UserEntity.builder()
                        .userId(userId)
                        .build())
                .spotInfo(SpotInfoEntity.builder()
                        .spotInfoId(spotInfoId)
                        .build())
                .build();
    }
}
