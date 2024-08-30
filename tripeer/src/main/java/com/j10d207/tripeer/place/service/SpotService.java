package com.j10d207.tripeer.place.service;

import com.j10d207.tripeer.place.db.dto.*;
import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import com.j10d207.tripeer.place.db.vo.SpotAddVO;

import java.util.List;

public interface SpotService {
    public SpotDetailPageDto getDetailMainPage(long userId, int spotInfoId);

    public List<ReviewDto> getReviewPage(int spotInfoId, int page);

    public SpotListDto getSpotSearch(int page, int ContentTypeId, int cityId, int townId, long userId);

    public SpotDetailDto getSpotDetail(Integer spotId);

    public SpotAddResDto createNewSpot(SpotAddVO spotAddVO, long userId);

    public void createNewDescrip(SpotInfoEntity spotInfoEntity, SpotAddVO spotAddVO);

    public void createNewDetail(SpotInfoEntity spotInfoEntity, SpotAddVO spotAddVO);
}
