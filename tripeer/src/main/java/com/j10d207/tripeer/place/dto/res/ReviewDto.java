package com.j10d207.tripeer.place.dto.res;

import com.j10d207.tripeer.place.db.entity.SpotReviewEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<String> img;


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
                .img(spotReviewEntity.createImageList())
                .build();

    }

}
