package com.j10d207.tripeer.place.db.entity;

import com.j10d207.tripeer.place.db.vo.ReviewVO;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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

    public static SpotReviewEntity ofReviewVO(ReviewVO reviewVO, long userId) {
        return  SpotReviewEntity.builder()
                .spotReviewId(reviewVO.getSpotReviewId())
                .spotInfo(SpotInfoEntity.builder().spotInfoId(reviewVO.getSpotInfoId()).build())
                .user(UserEntity.builder().userId(userId).build())
                .starPoint(reviewVO.getStarPoint())
                .message(reviewVO.getMessage())
                .createTime(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .image1(reviewVO.getImage1())
                .image2(reviewVO.getImage2())
                .image3(reviewVO.getImage3())
                .image4(reviewVO.getImage4())
                .build();
    }

}
