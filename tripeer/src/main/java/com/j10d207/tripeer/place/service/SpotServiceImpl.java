package com.j10d207.tripeer.place.service;

import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.kakao.db.entity.BlogInfoResponse;
import com.j10d207.tripeer.kakao.service.KakaoService;
import com.j10d207.tripeer.place.db.dto.*;
import com.j10d207.tripeer.place.db.entity.*;
import com.j10d207.tripeer.place.db.repository.*;
import com.j10d207.tripeer.place.db.repository.additional.AdditionalBaseRepository;
import com.j10d207.tripeer.place.db.vo.SpotAddVO;
import com.j10d207.tripeer.plan.service.PlanService;
import com.j10d207.tripeer.user.db.repository.WishListRepository;
import com.j10d207.tripeer.user.dto.res.UserDTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpotServiceImpl implements SpotService{

    private final SpotInfoRepository spotInfoRepository;
    private final SpotDescriptionRepository spotDescriptionRepository;
    private final SpotDetailRepository spotDetailRepository;
    private final CityRepository cityRepository;
    private final TownRepository townRepository;
    private final SpotReviewRepository spotReviewRepository;
    private final PlanService planService;
    private final KakaoService kakaoService;
    private final WishListRepository wishListRepository;
    private final AdditionalBaseRepository additionalBaseRepository;
    private final SpotCollectionRepository spotCollectionRepository;


    private List<SpotInfoDto> convertToDtoList(List<SpotInfoEntity> spotInfoEntities, long userId) {
        List<SpotInfoDto> spotInfoDtos = new ArrayList<>();
        for (SpotInfoEntity spotInfoEntity : spotInfoEntities) {
            boolean isWishlist = wishListRepository.existsByUser_UserIdAndSpotInfo_SpotInfoId(userId, spotInfoEntity.getSpotInfoId());
            spotInfoDtos.add(SpotInfoDto.convertToDto(spotInfoEntity, isWishlist));
        }
        return spotInfoDtos;
    }

    @Override
    public SpotDetailPageDto getDetailMainPage(long userId, int spotInfoId) {
        SpotInfoEntity spotInfoEntity = spotInfoRepository.findBySpotInfoId(spotInfoId);

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<SpotReviewEntity> spotReviewEntityPage = spotReviewRepository.findBySpotInfo_SpotInfoId(spotInfoId, pageable);

        SpotDetailPageDto spotDetailPageDto = SpotDetailPageDto.createDto(spotInfoEntity, spotReviewEntityPage, getBlogSearchInfo(spotInfoEntity.getTitle(), 1));
        Set<Integer> wishList = wishListRepository.findByUser_UserId(userId).stream()
            .map(el -> el.getSpotInfo().getSpotInfoId())
            .collect(Collectors.toSet());
        spotDetailPageDto.setLike(wishListRepository.existsByUser_UserIdAndSpotInfo_SpotInfoId(userId, spotInfoId));
        spotDetailPageDto.setOverview(spotDescriptionRepository.findBySpotInfo(spotInfoEntity).getOverview());

        spotReviewRepository.findAverageStarPointBySpotInfoId(spotInfoEntity.getSpotInfoId())
                .map(starPoint -> Math.round(starPoint * 10) / 10.0)
                    .ifPresentOrElse(spotDetailPageDto::setStarPointAvg,
                            () -> spotDetailPageDto.setStarPointAvg(0)
                    );
        spotDetailPageDto.setAdditionalInfo(AdditionalDto.from(additionalBaseRepository.findBySpotInfo(spotInfoEntity)));

        SpotCollectionEntity spotCollection = spotCollectionRepository.findBySpotInfoId(spotInfoEntity.getSpotInfoId());
        System.out.println(spotInfoEntity.getSpotInfoId());
        System.out.println(spotCollection);
        spotDetailPageDto.setSimilarSpotList(spotCollection.getSimSpotIdList().stream().map(
			spotInfoRepository::findBySpotInfoId).map(el -> SpotInfoDto.convertToDto(el, wishList.contains(el.getSpotInfoId()))).toList());

        spotDetailPageDto.setNearSpotList(spotCollection.getNearSpotIdList().stream().map(
            spotInfoRepository::findBySpotInfoId).map(el -> SpotInfoDto.convertToDto(el, wishList.contains(el.getSpotInfoId()))).toList());
        return spotDetailPageDto;
    }


    @Override
    public List<ReviewDto> getReviewPage(int spotInfoId, int page) {
        if(page < 1) throw new CustomException(ErrorCode.INVALID_PAGE);
        Pageable pageable = PageRequest.of(page-1, 5, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<SpotReviewEntity> spotReviewEntityPage = spotReviewRepository.findBySpotInfo_SpotInfoId(spotInfoId, pageable);
        Page<ReviewDto> reviewDtoPage = spotReviewEntityPage.map(ReviewDto::fromEntity);
        return reviewDtoPage.getContent();
    }

    @Override
    public List<BlogInfoResponse.Document> getBlogInfoPage(String query, int page) {
        return getBlogSearchInfo(query, page);
    }

    @Override
    public SpotListDto getSpotSearch(int page, int ContentTypeId, int cityId, int townId, long userId) {
        if (ContentTypeId == 32 || ContentTypeId == 39) {
            return getSpotByContentType(page, ContentTypeId, cityId, townId, userId);
        } else if (ContentTypeId == 100) {
            return getSpotByContentType(page, Arrays.asList(32, 39), cityId, townId, userId);
        } else if (ContentTypeId == -1) {
            return getSpotByContentType(page, cityId, townId, userId);
        } else {
            throw new CustomException(ErrorCode.UNDEFINED_TYPE);
        }
    }

    private SpotListDto getSpotByContentType(Integer page, Integer ContentTypeId, Integer cityId, Integer townId, long userId) {
        Pageable pageable = PageRequest.of(page,15);

        List<SpotInfoEntity> spotInfoEntities;
        if (townId == -1 && cityId == -1) {
            spotInfoEntities = spotInfoRepository.findByContentTypeId(ContentTypeId, pageable);
        } else if (townId == -1) {
            spotInfoEntities = spotInfoRepository.findByContentTypeIdAndTown_TownPK_City_CityId(ContentTypeId, cityId, pageable);
        } else {
            spotInfoEntities = spotInfoRepository.findByContentTypeIdAndTown_TownPK_City_CityIdAndTown_TownPK_TownId(ContentTypeId, cityId, townId, pageable);
        }

        List<SpotInfoDto> spotInfoDtos = convertToDtoList(spotInfoEntities, userId);

        boolean isLastPage = spotInfoDtos.size() < 15;

        return SpotListDto.builder()
                .spotInfoDtos(spotInfoDtos)
                .last(isLastPage)
                .build();
    }

    private SpotListDto getSpotByContentType(Integer page, List<Integer> ContentTypeId, Integer cityId, Integer townId, long userId) {
        Pageable pageable = PageRequest.of(page,15);
        List<SpotInfoEntity> spotInfoEntities;
        if (townId == -1 && cityId == -1) {
            spotInfoEntities = spotInfoRepository.findByContentTypeIdNotIn(ContentTypeId, pageable);
        } else if (townId == -1) {
            spotInfoEntities = spotInfoRepository.findByContentTypeIdNotInAndTown_TownPK_City_CityId(ContentTypeId, cityId, pageable);
        } else {
            spotInfoEntities = spotInfoRepository.findByContentTypeIdNotInAndTown_TownPK_City_CityIdAndTown_TownPK_TownId(ContentTypeId, cityId, townId, pageable);
        }

        List<SpotInfoDto> spotInfoDtos = convertToDtoList(spotInfoEntities, userId);

        boolean isLastPage = spotInfoDtos.size() < 15;

        return SpotListDto.builder()
                .spotInfoDtos(spotInfoDtos)
                .last(isLastPage)
                .build();
    }

    private SpotListDto getSpotByContentType(Integer page, Integer cityId, Integer townId, long userId) {
        Pageable pageable = PageRequest.of(page,5);

        List<SpotInfoEntity> spotInfoEntities;
        if (townId == -1 && cityId == -1) {
            spotInfoEntities = spotInfoRepository.findByContentTypeId(32, pageable);
            spotInfoEntities.addAll(spotInfoRepository.findByContentTypeId(39, pageable));
            spotInfoEntities.addAll(spotInfoRepository.findByContentTypeIdNotIn(Arrays.asList(32, 39), pageable));
        } else if (townId == -1) {
            spotInfoEntities = spotInfoRepository.findByContentTypeIdAndTown_TownPK_City_CityId(32, cityId, pageable);
            spotInfoEntities.addAll(spotInfoRepository.findByContentTypeIdAndTown_TownPK_City_CityId(39, cityId, pageable));
            spotInfoEntities.addAll(spotInfoRepository.findByContentTypeIdNotInAndTown_TownPK_City_CityId(Arrays.asList(32, 39), cityId, pageable));
        } else {
            spotInfoEntities = spotInfoRepository.findByContentTypeIdAndTown_TownPK_City_CityIdAndTown_TownPK_TownId(32, cityId, townId, pageable);
            spotInfoEntities.addAll(spotInfoRepository.findByContentTypeIdAndTown_TownPK_City_CityIdAndTown_TownPK_TownId(39, cityId, townId, pageable));
            spotInfoEntities.addAll(spotInfoRepository.findByContentTypeIdNotInAndTown_TownPK_City_CityIdAndTown_TownPK_TownId(Arrays.asList(32, 39), cityId, townId, pageable));
        }

        List<SpotInfoDto> spotInfoDtos = convertToDtoList(spotInfoEntities, userId);

        boolean isLastPage = spotInfoDtos.size() < 5;

        return SpotListDto.builder()
                .spotInfoDtos(spotInfoDtos)
                .last(isLastPage)
                .build();
    }

    @Override
    public SpotDetailDto getSpotDetail(Integer spotId) {
        SpotInfoEntity spotInfoEntity = spotInfoRepository.findById(spotId)
                .orElseThrow(() -> new CustomException(ErrorCode.SPOT_NOT_FOUND));

        return SpotDetailDto.convertToDto(spotDescriptionRepository.findBySpotInfo(spotInfoEntity));
    }


    @Override
    @Transactional
    public void createNewDescrip(SpotInfoEntity spotInfoEntity, SpotAddVO spotAddVO) {
        SpotDescriptionEntity build = SpotDescriptionEntity.builder()
                .spotInfo(spotInfoEntity)
                .overview(spotAddVO.getOverview())
                .build();
        spotDescriptionRepository.save(build);
        createNewDetail(spotInfoEntity, spotAddVO);
    }

    @Override
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

        SpotDetailEntity spotDetail = SpotDetailEntity.builder()
                .spotInfo(spotInfoEntity)
                .cat1(cat1)
                .cat2(cat2)
                .cat3(cat3)
                .createdTime("0000")
                .modifiedTime("0000")
                .build();

        spotDetailRepository.save(spotDetail);
    }


    public CityEntity getCityEntity(String splitAddr) {
        
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
    
    
    @Override
    @Transactional
    public SpotAddResDto createNewSpot(SpotAddVO spotAddVO, long userId) {

//        1. city 찾기
        String fullAddr = spotAddVO.getAddr1();

        String[] splitAddr = fullAddr.split(" ");
        CityEntity cityEntity = getCityEntity(splitAddr[0]);
        TownEntity townEntity = null;

        Optional<TownEntity> townEntityOptional = townRepository.findByTownNameAndTownPK_City_CityId(splitAddr[1], cityEntity.getCityId());
        if (townEntityOptional.isPresent()) {
            townEntity = townEntityOptional.get();
        } else {
            TownPK townPK = TownPK.builder()
                    .city(cityEntity)
                    .townId(townRepository.findMaxTownId() + 1)
                    .build();

            townEntity = TownEntity.builder()
                    .townName(splitAddr[1])
                    .longitude(spotAddVO.getLongitude())
                    .latitude(spotAddVO.getLatitude())
                    .description("discription")
                    .townImg("https://tripeer207.s3.ap-northeast-2.amazonaws.com/front/static/default1.png")
                    .townPK(townPK)
                    .build();
            townRepository.save(townEntity);
        }

        StringBuilder newAddr = new StringBuilder(cityEntity.getCityName() + " " + townEntity.getTownName() + " ");

        for (int i = 2;  i < splitAddr.length; i+=1) {
            if (i != splitAddr.length-1) {
                newAddr.append(splitAddr[i]).append(" ");
            } else {
                newAddr.append(splitAddr[i]);
            }
        }


        SpotInfoEntity spotInfo = SpotInfoEntity.MakeNewSpotEntity(spotAddVO, townEntity, newAddr.toString());


        SpotInfoEntity newSpotInfo = spotInfoRepository.save(spotInfo);

        createNewDescrip(newSpotInfo, spotAddVO);

        if(spotAddVO.isAddPlanCheck()) {
            planService.addPlanSpot(spotAddVO.getPlanId(), newSpotInfo.getSpotInfoId(), userId);
        }

        return SpotAddResDto.EntityToDTO(spotInfo, spotAddVO.isAddPlanCheck());

    }


    private List<BlogInfoResponse.Document> getBlogSearchInfo(String query, int page) {
        return kakaoService.getBlogInfo(query, "accuracy", page, 5).getDocuments();
    }
}
