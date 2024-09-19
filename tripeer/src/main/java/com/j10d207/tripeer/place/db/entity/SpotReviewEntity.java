package com.j10d207.tripeer.place.db.entity;

import com.j10d207.tripeer.place.dto.req.ReviewReq;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity(name = "spot_review")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // PK
    private long spotReviewId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPOT_INFO_ID")
    private SpotInfoEntity spotInfo;

    private double starPoint;
    private String message;
    private LocalDateTime createTime;
    private String image1;
    private String image2;
    private String image3;
    private String image4;

    public List<String> createImageList() {
        return Stream.of(image1, image2, image3, image4)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static SpotReviewEntity ofReviewReq(ReviewReq reviewReq, long userId) {
        return  SpotReviewEntity.builder()
                .spotReviewId(reviewReq.getSpotReviewId())
                .spotInfo(SpotInfoEntity.builder().spotInfoId(reviewReq.getSpotInfoId()).build())
                .user(UserEntity.builder().userId(userId).build())
                .starPoint(reviewReq.getStarPoint())
                .message(reviewReq.getMessage())
                .createTime(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .image1(reviewReq.getImg().isEmpty() ? null : reviewReq.getImg().poll())
                .image2(reviewReq.getImg().isEmpty() ? null : reviewReq.getImg().poll())
                .image3(reviewReq.getImg().isEmpty() ? null : reviewReq.getImg().poll())
                .image4(reviewReq.getImg().isEmpty() ? null : reviewReq.getImg().poll())
                .build();
    }

}
