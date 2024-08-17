package com.j10d207.tripeer.place.service;

import com.j10d207.tripeer.place.db.dto.SpotAddResDto;
import com.j10d207.tripeer.place.db.dto.SpotDetailDto;
import com.j10d207.tripeer.place.db.dto.SpotListDto;
import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import com.j10d207.tripeer.place.db.vo.SpotAddVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface SpotService {
    public SpotListDto getSpotByContentType(Integer page, Integer ContentTypeId, Integer cityId, Integer townId, long userId);

    public SpotListDto getSpotByContentType(Integer page, List<Integer> ContentTypeId, Integer cityId, Integer townId, long userId);

    public SpotDetailDto getSpotDetail(Integer spotId);

    public SpotAddResDto createNewSpot(SpotAddVO spotAddVO, long userId);

    public void createNewDescrip(SpotInfoEntity spotInfoEntity, SpotAddVO spotAddVO);

    public void createNewDetail(SpotInfoEntity spotInfoEntity, SpotAddVO spotAddVO);
}
