package com.j10d207.tripeer.place.db.dto;

import com.j10d207.tripeer.place.db.entity.SpotReviewEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewDto {

    private long spotReviewId;
    private long userId;
    private int spotInfoId;

    private String nickname;
    private String profileImage;

    private double starPoint;
    private String message;
    private LocalDateTime createTime;
    private String image1;
    private String image2;
    private String image3;
    private String image4;


    public static ReviewDto fromEntity(SpotReviewEntity spotReviewEntity) {
        return ReviewDto.builder()
                .spotReviewId(spotReviewEntity.getSpotReviewId())
                .userId(spotReviewEntity.getUser().getUserId())
                .spotInfoId(spotReviewEntity.getSpotInfo().getSpotInfoId())
                .nickname(spotReviewEntity.getUser().getNickname())
                .profileImage(spotReviewEntity.getUser().getProfileImage())
                .starPoint(spotReviewEntity.getStarPoint())
                .message(spotReviewEntity.getMessage())
                .createTime(spotReviewEntity.getCreateTime())
                .image1(spotReviewEntity.getImage1())
                .image2(spotReviewEntity.getImage2())
                .image3(spotReviewEntity.getImage3())
                .image4(spotReviewEntity.getImage4())
                .build();

    }

}
