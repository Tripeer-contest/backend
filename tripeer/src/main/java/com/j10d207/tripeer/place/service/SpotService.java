package com.j10d207.tripeer.place.service;

import com.j10d207.tripeer.kakao.db.entity.BlogInfoResponse;
import com.j10d207.tripeer.place.dto.req.SpotAddReq;
import com.j10d207.tripeer.place.dto.res.ReviewDto;
import com.j10d207.tripeer.place.dto.res.SpotDTO;
import com.j10d207.tripeer.place.dto.res.SpotDetailPageDto;

import java.util.List;

public interface SpotService {
    public SpotDetailPageDto getDetailMainPage(long userId, int spotInfoId);

    public List<ReviewDto> getReviewPage(int spotInfoId, int page);

    public List<BlogInfoResponse.Document> getBlogInfoPage(String query, int page);

    public SpotDTO.SpotListDTO getSpotSearch(int page, int ContentTypeId, int cityId, int townId, long userId);

/*새 장소 등록 코드, 원본 작성자 퇴사 + 리뉴얼 제작성 하는걸로 자체 결정, 새로 쓸때 참고용으로 주석처리 해둠
    public SpotDTO.SpotAddResDTO createNewSpot(SpotAddReq spotAddReq, long userId);

    public void createNewDescrip(SpotInfoEntity spotInfoEntity, SpotAddVO spotAddVO);

    public void createNewDetail(SpotInfoEntity spotInfoEntity, SpotAddVO spotAddVO);
*/

}
