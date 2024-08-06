package com.j10d207.tripeer.place.service;

import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.place.db.dto.*;
import com.j10d207.tripeer.place.db.entity.*;
import com.j10d207.tripeer.place.db.repository.*;
import com.j10d207.tripeer.place.db.vo.SpotAddVO;
import com.j10d207.tripeer.plan.service.PlanService;
import com.j10d207.tripeer.user.config.JWTUtil;
import com.j10d207.tripeer.user.db.repository.WishListRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpotServiceImpl implements SpotService{

    private final SpotInfoRepository spotInfoRepository;
    private final SpotDescriptionRepository spotDescriptionRepository;
    private final SpotDetailRepository spotDetailRepository;
    private final CityRepository cityRepository;
    private final TownRepository townRepository;
    private final PlanService planService;
    private final WishListRepository wishListRepository;

    private List<SpotDTO.SpotInfoDTO> convertToDtoList(List<SpotInfoEntity> spotInfoEntities, long userId) {
        List<SpotDTO.SpotInfoDTO> spotInfoDTOList = new ArrayList<>();
        for (SpotInfoEntity spotInfoEntity : spotInfoEntities) {
            boolean isWishlist = wishListRepository.existsByUser_UserIdAndSpotInfo_SpotInfoId(userId, spotInfoEntity.getSpotInfoId());
            spotInfoDTOList.add(SpotDTO.SpotInfoDTO.convertToDto(spotInfoEntity, isWishlist));
        }
        return spotInfoDTOList;
    }

    @Override
    public SpotDTO.SpotListDTO getSpotByContentType(Integer page, Integer ContentTypeId, Integer cityId, Integer townId, long userId) {
        Pageable pageable = PageRequest.of(page,15);

        List<SpotInfoEntity> spotInfoEntities;
        if (townId == -1) {
            spotInfoEntities = spotInfoRepository.findByContentTypeIdAndTown_TownPK_City_CityId(ContentTypeId, cityId, pageable);
        } else {
            spotInfoEntities = spotInfoRepository.findByContentTypeIdAndTown_TownPK_City_CityIdAndTown_TownPK_TownId(ContentTypeId, cityId, townId, pageable);
        }

        List<SpotDTO.SpotInfoDTO> spotInfoDTOList = convertToDtoList(spotInfoEntities, userId);

        return new SpotDTO.SpotListDTO(spotInfoDTOList.size() < 15, spotInfoDTOList);
    }

    @Override
    public SpotDTO.SpotListDTO getSpotByContentType(Integer page, List<Integer> ContentTypeId, Integer cityId, Integer townId, long userId) {
        Pageable pageable = PageRequest.of(page,15);
        List<SpotInfoEntity> spotInfoEntities;
        if (townId == -1) {
            spotInfoEntities = spotInfoRepository.findByContentTypeIdNotInAndTown_TownPK_City_CityId(ContentTypeId, cityId, pageable);
        } else {
            spotInfoEntities = spotInfoRepository.findByContentTypeIdNotInAndTown_TownPK_City_CityIdAndTown_TownPK_TownId(ContentTypeId, cityId, townId, pageable);
        }

        List<SpotDTO.SpotInfoDTO> spotInfoDTOList = convertToDtoList(spotInfoEntities, userId);

        return new SpotDTO.SpotListDTO(spotInfoDTOList.size() < 15, spotInfoDTOList);
    }

    @Override
    public SpotDTO.SpotDetailDTO getSpotDetail(Integer spotId) {
        SpotInfoEntity spotInfoEntity = spotInfoRepository.findById(spotId)
                .orElseThrow(() -> new CustomException(ErrorCode.SPOT_NOT_FOUND));

        return SpotDTO.SpotDetailDTO.convertToDto(spotDescriptionRepository.findBySpotInfo(spotInfoEntity));
    }

    @Override
    @Transactional
    public SpotDTO.SpotAddResDTO createNewSpot(SpotAddVO spotAddVO, long userId) {
//        1. city 찾기
        String[] splitAddr = spotAddVO.getAddr1().split(" ");
        CityEntity cityEntity = getCityEntity(splitAddr[0]);
        TownEntity townEntity = null;

        Optional<TownEntity> townEntityOptional = townRepository.findByTownNameAndTownPK_City_CityId(splitAddr[1], cityEntity.getCityId());
        if (townEntityOptional.isPresent()) {
            townEntity = townEntityOptional.get();
        } else {
            townRepository.save(TownEntity.MakeNewSpotTownEntity(spotAddVO, splitAddr[1], new TownPK(townRepository.findMaxTownId() + 1, cityEntity)));
        }

        SpotInfoEntity spotInfo = SpotInfoEntity.MakeNewSpotEntity(spotAddVO, townEntity, MakeNewAddr(cityEntity.getCityName(), townEntity.getTownName(), splitAddr).toString());
        SpotInfoEntity newSpotInfo = spotInfoRepository.save(spotInfo);
        createNewDescrip(newSpotInfo, spotAddVO);

        if(spotAddVO.isAddPlanCheck()) {
            planService.addPlanSpot(spotAddVO.getPlanId(), newSpotInfo.getSpotInfoId(), userId);
        }

        return SpotDTO.SpotAddResDTO.EntityToDTO(spotInfo, spotAddVO.isAddPlanCheck());
    }

    private StringBuilder MakeNewAddr (String cityName, String TownName, String[] splitAddr) {
        StringBuilder newAddr = new StringBuilder(cityName + " " + TownName + " ");

        for (int i = 2;  i < splitAddr.length; i+=1) {
            if (i != splitAddr.length-1) {
                newAddr.append(splitAddr[i]).append(" ");
            } else {
                newAddr.append(splitAddr[i]);
            }
        }

        return newAddr;
    }

    private CityEntity getCityEntity(String splitAddr) {

        Optional<CityEntity> CityEntityOptional = cityRepository.findByCityNameContains(splitAddr);
        CityEntity cityEntity = null;

        if (CityEntityOptional.isPresent()) {
            cityEntity = CityEntityOptional.get();
        } else {
            switch (splitAddr) {
                case "강원특별자치도": //
                    cityEntity = cityRepository.findByCityNameContains("강원도")
                            .orElseThrow(() -> new CustomException(ErrorCode.CITY_NOT_FOUND));
                    break;
                case "충북":
                    cityEntity = cityRepository.findByCityNameContains("충청북도")
                            .orElseThrow(() -> new CustomException(ErrorCode.CITY_NOT_FOUND));
                    break;
                case "충남":
                    cityEntity = cityRepository.findByCityNameContains("충청남도")
                            .orElseThrow(() -> new CustomException(ErrorCode.CITY_NOT_FOUND));
                    break;
                case "경북":
                    cityEntity = cityRepository.findByCityNameContains("경상북도")
                            .orElseThrow(() -> new CustomException(ErrorCode.CITY_NOT_FOUND));
                    break;
                case "경남":
                    cityEntity = cityRepository.findByCityNameContains("경상남도")
                            .orElseThrow(() -> new CustomException(ErrorCode.CITY_NOT_FOUND));
                    break;
                case "전북특별자치도":
                    cityEntity = cityRepository.findByCityNameContains("전라북도")
                            .orElseThrow(() -> new CustomException(ErrorCode.CITY_NOT_FOUND));
                    break;
                case "전남":
                    cityEntity = cityRepository.findByCityNameContains("전라남도")
                            .orElseThrow(() -> new CustomException(ErrorCode.CITY_NOT_FOUND));
                    break;
                case "제주특별자치도":
                    cityEntity = cityRepository.findByCityNameContains("제주도")
                            .orElseThrow(() -> new CustomException(ErrorCode.CITY_NOT_FOUND));
                    break;
                default:
                    break;
            }
        }

        return cityEntity;
    }

    //    @Override
    @Transactional
    private void createNewDescrip(SpotInfoEntity spotInfoEntity, SpotAddVO spotAddVO) {
        SpotDescriptionEntity build = SpotDescriptionEntity.builder()
                .spotInfo(spotInfoEntity)
                .overview(spotAddVO.getOverview())
                .build();
        spotDescriptionRepository.save(build);
        createNewDetail(spotInfoEntity, spotAddVO);
    }

    //    @Override
    @Transactional
    public void createNewDetail(SpotInfoEntity spotInfoEntity, SpotAddVO spotAddVO) {

        String cat1 = null;
        String cat2 = null;
        String cat3 = null;
        switch (spotAddVO.getContentTypeId()) {
            //숙소
            case 32 -> {
                cat1 = "B02";
                cat2 = "B0201";
                cat3 = "B02010100";
            }
            //식당
            case 39 -> {
                cat1 = "A05";
                cat2 = "A0502";
                cat3 = "A05020100";
            }
            //관광지
            default -> {
                cat1 = "A01";
                cat2 = "A0101";
                cat3 = "A01010100";
            }
        }

        spotDetailRepository.save(SpotDetailEntity.MakeNewSpotDetailEntity(spotInfoEntity, cat1, cat2, cat3));
    }
}
