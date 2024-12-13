package com.j10d207.tripeer.place.dto.res;

import com.j10d207.tripeer.kakao.db.entity.BlogInfoResponse;
import com.j10d207.tripeer.place.db.ContentTypeEnum;
import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import com.j10d207.tripeer.place.db.entity.SpotReviewEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

/*
장소 자세히 보기 페이지의 메인 로딩시 전달되는 데이터 Response
 */
@Getter
@Setter
@Builder
public class SpotDetailPageDto {

    private String title;
    private List<String> imageList;
    private String contentType;
    private boolean isLike;
    private double starPointAvg;
    private List<ReviewDto> reviewDtoList;
    private int reviewPageCount;
    private long reviewTotalCount;

    private List<BlogInfoResponse.Document> blogInfoList;

    private double latitude;
    private double longitude;

    private String addr1;

    private String overview;

    private List<AdditionalDto> additionalInfo;   // 상세 정보

    private List<SpotDTO.SpotInfoDTO> similarSpotList;   // 비슷한 관광지

    private List<SpotDTO.SpotInfoDTO> nearSpotList;   // 가까운 관광지


    public static SpotDetailPageDto createDto (SpotInfoEntity spotInfoEntity, Page<SpotReviewEntity> spotReviewEntityPage, List<BlogInfoResponse.Document> blogInfoList) {
        Page<ReviewDto> reviewDtoPage = spotReviewEntityPage.map(ReviewDto::fromEntity);
        return SpotDetailPageDto.builder()
                .title(spotInfoEntity.getTitle())
                .imageList(spotInfoEntity.createImageList())
                .contentType(ContentTypeEnum.getNameByCode(spotInfoEntity.getContentTypeId()))
                .reviewDtoList(reviewDtoPage.getContent())
                .reviewPageCount(spotReviewEntityPage.getTotalPages())
                .reviewTotalCount(spotReviewEntityPage.getTotalElements())
                .blogInfoList(blogInfoList)
                .latitude(spotInfoEntity.getLatitude())
                .longitude(spotInfoEntity.getLongitude())
                .addr1(spotInfoEntity.getAddr1())
                .starPointAvg(spotInfoEntity.getStarPointAvg())
                .build();
    }
}
