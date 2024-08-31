package com.j10d207.tripeer.place.db.dto;

import com.j10d207.tripeer.place.db.ContentTypeEnum;
import com.j10d207.tripeer.place.db.entity.AdditionalBaseEntity;
import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import com.j10d207.tripeer.place.db.entity.SpotReviewEntity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/*
장소 자세히 보기 페이지의 메인 로딩시 전달되는 데이터 Response
 */
@Getter
@Builder
public class SpotDetailPageDto {

    private String title;
    private String img;
    private String contentType;
    private boolean isLike;
    private List<ReviewDto> reviewDtoList;
    private int reviewPageCount;

    private double latitude;
    private double longitude;

    private List<AdditionalDto> detailInfoList;

    private String addr1;

    private String overview;

    public static SpotDetailPageDto createDto (SpotInfoEntity spotInfoEntity,
                                                boolean isLike,
                                                Page<SpotReviewEntity> spotReviewEntityPage,
                                                String overview, List<AdditionalDto> detailInfoList ) {
        Page<ReviewDto> reviewDtoPage = spotReviewEntityPage.map(ReviewDto::fromEntity);
        return SpotDetailPageDto.builder()
                .title(spotInfoEntity.getTitle())
                .img(spotInfoEntity.getFirstImage())
                .contentType(ContentTypeEnum.getNameByCode(spotInfoEntity.getContentTypeId()))
                .isLike(isLike)
                .reviewDtoList(reviewDtoPage.getContent())
                .reviewPageCount(spotReviewEntityPage.getTotalPages())
                .latitude(spotInfoEntity.getLatitude())
                .longitude(spotInfoEntity.getLongitude())
                .detailInfoList(detailInfoList)
                .addr1(spotInfoEntity.getAddr1())
                .overview(overview)
                .build();
    }
}
