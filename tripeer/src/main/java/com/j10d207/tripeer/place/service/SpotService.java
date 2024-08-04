package com.j10d207.tripeer.place.service;

import com.j10d207.tripeer.place.db.dto.SpotDTO;
import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import com.j10d207.tripeer.place.db.vo.SpotAddVO;

import java.util.List;

public interface SpotService {
    public SpotDTO.SpotListDTO getSpotByContentType(Integer page, Integer ContentTypeId, Integer cityId, Integer townId, long userId);

    public SpotDTO.SpotListDTO getSpotByContentType(Integer page, List<Integer> ContentTypeId, Integer cityId, Integer townId, long userId);

    public SpotDTO.SpotDetailDTO getSpotDetail(Integer spotId);

    public SpotDTO.SpotAddResDTO createNewSpot(SpotAddVO spotAddVO, long userId);

//    public void createNewDescrip(SpotInfoEntity spotInfoEntity, SpotAddVO spotAddVO);

    public void createNewDetail(SpotInfoEntity spotInfoEntity, SpotAddVO spotAddVO);
}
