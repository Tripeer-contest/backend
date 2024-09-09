package com.j10d207.tripeer.place.service;

import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.kakao.db.entity.BlogInfoResponse;
import com.j10d207.tripeer.kakao.service.KakaoService;
import com.j10d207.tripeer.place.db.ContentTypeEnum;
import com.j10d207.tripeer.place.db.entity.*;
import com.j10d207.tripeer.place.db.repository.*;
import com.j10d207.tripeer.place.db.repository.additional.AdditionalBaseRepository;
import com.j10d207.tripeer.place.dto.res.AdditionalDto;
import com.j10d207.tripeer.place.dto.res.ReviewDto;
import com.j10d207.tripeer.place.dto.res.SpotDTO;
import com.j10d207.tripeer.place.dto.res.SpotDetailPageDto;
import com.j10d207.tripeer.user.db.repository.WishListRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpotServiceImpl implements SpotService{

    private final SpotInfoRepository spotInfoRepository;
    private final SpotDescriptionRepository spotDescriptionRepository;
    private final SpotReviewRepository spotReviewRepository;
    private final KakaoService kakaoService;
    private final WishListRepository wishListRepository;
    private final AdditionalBaseRepository additionalBaseRepository;
    private final SpotCollectionRepository spotCollectionRepository;

    private static final int ALL = -1;
    private static final int SPOT_SEARCH_PER_PAGE = 15;
    private static final int REVIEW_PER_PAGE = 5;
    private static final int BLOG_PER_PAGE = 5;


    @Override
    public SpotDetailPageDto getDetailMainPage(long userId, int spotInfoId) {
        SpotInfoEntity spotInfoEntity = spotInfoRepository.findBySpotInfoId(spotInfoId);

        Pageable pageable = PageRequest.of(0, REVIEW_PER_PAGE, Sort.by(Sort.Direction.DESC, "createTime"));
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
        spotDetailPageDto.setSimilarSpotList(spotCollection.getSimSpotIdList().stream().map(
			spotInfoRepository::findBySpotInfoId).map(el -> SpotDTO.SpotInfoDTO.convertToDto(el, wishList.contains(el.getSpotInfoId()))).toList());

        spotDetailPageDto.setNearSpotList(spotCollection.getNearSpotIdList().stream().map(
            spotInfoRepository::findBySpotInfoId).map(el -> SpotDTO.SpotInfoDTO.convertToDto(el, wishList.contains(el.getSpotInfoId()))).toList());
        return spotDetailPageDto;
    }


    //특정 페이지 리뷰 가져오기
    @Override
    public List<ReviewDto> getReviewPage(int spotInfoId, int page) {
        if(page < 1) throw new CustomException(ErrorCode.INVALID_PAGE);
        Pageable pageable = PageRequest.of(page-1, REVIEW_PER_PAGE, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<SpotReviewEntity> spotReviewEntityPage = spotReviewRepository.findBySpotInfo_SpotInfoId(spotInfoId, pageable);
        Page<ReviewDto> reviewDtoPage = spotReviewEntityPage.map(ReviewDto::fromEntity);
        return reviewDtoPage.getContent();
    }

    //블로그 검색결과의 특정 페이지 가져오기 (더보기 용도)
    @Override
    public List<BlogInfoResponse.Document> getBlogInfoPage(String query, int page) {
        return getBlogSearchInfo(query, page);
    }

    // 도시, 구 선택 혹은 타입 등의 옵션으로 장소를 받아오기
    @Override
    public SpotDTO.SpotListDTO getSpotSearch(int page, int ContentTypeId, int cityId, int townId, long userId) {
        ContentTypeEnum contentTypeEnum = ContentTypeEnum.getByCode(ContentTypeId);
        return switch (contentTypeEnum) {
            case ACCOMMODATION, RESTAURANT -> getSpotByContentType(page, ContentTypeId, cityId, townId, userId);
            case MECCA -> getSpotByContentType(page, Arrays.asList(32, 39), cityId, townId, userId);
            case ALL_SPOT -> getSpotByContentType(page, cityId, townId, userId);
            default -> throw new CustomException(ErrorCode.UNDEFINED_TYPE);
        };
    }

    //타입별 오버로딩, -1은 전체를 의미
    private SpotDTO.SpotListDTO getSpotByContentType(Integer page, Integer ContentTypeId, Integer cityId, Integer townId, long userId) {
        Pageable pageable = PageRequest.of(page, SPOT_SEARCH_PER_PAGE);

        List<SpotInfoEntity> spotInfoEntities;
        if (townId == ALL && cityId == ALL) {
            spotInfoEntities = spotInfoRepository.findByContentTypeId(ContentTypeId, pageable);
        } else if (townId == ALL) {
            spotInfoEntities = spotInfoRepository.findByContentTypeIdAndTown_TownPK_City_CityId(ContentTypeId, cityId, pageable);
        } else {
            spotInfoEntities = spotInfoRepository.findByContentTypeIdAndTown_TownPK_City_CityIdAndTown_TownPK_TownId(ContentTypeId, cityId, townId, pageable);
        }

        List<SpotDTO.SpotInfoDTO> spotInfoDTOList = convertToDtoList(spotInfoEntities, userId);

        return new SpotDTO.SpotListDTO(spotInfoDTOList.size() < SPOT_SEARCH_PER_PAGE, spotInfoDTOList);
    }

    //타입별 오버로딩, -1은 전체를 의미
    private SpotDTO.SpotListDTO getSpotByContentType(Integer page, List<Integer> ContentTypeId, Integer cityId, Integer townId, long userId) {
        Pageable pageable = PageRequest.of(page, SPOT_SEARCH_PER_PAGE);
        List<SpotInfoEntity> spotInfoEntities;
        if (townId == ALL && cityId == ALL) {
            spotInfoEntities = spotInfoRepository.findByContentTypeIdNotIn(ContentTypeId, pageable);
        } else if (townId == ALL) {
            spotInfoEntities = spotInfoRepository.findByContentTypeIdNotInAndTown_TownPK_City_CityId(ContentTypeId, cityId, pageable);
        } else {
            spotInfoEntities = spotInfoRepository.findByContentTypeIdNotInAndTown_TownPK_City_CityIdAndTown_TownPK_TownId(ContentTypeId, cityId, townId, pageable);
        }

        List<SpotDTO.SpotInfoDTO> spotInfoDTOList = convertToDtoList(spotInfoEntities, userId);

        return new SpotDTO.SpotListDTO(spotInfoDTOList.size() < SPOT_SEARCH_PER_PAGE, spotInfoDTOList);
    }

    //타입별 오버로딩, -1은 전체를 의미
    private SpotDTO.SpotListDTO getSpotByContentType(Integer page, Integer cityId, Integer townId, long userId) {
        Pageable pageable = PageRequest.of(page, SPOT_SEARCH_PER_PAGE /3);

        //전체 검색의 경우 결과를 타입별로 다양성 있게 반환하기 위해 다음과 같이 작성, 요소 별 5개씩 반환 0개인경우 고려X 0~15개 유동적 갯수반환
        List<SpotInfoEntity> spotInfoEntities;
        if (townId == ALL && cityId == ALL) {
            spotInfoEntities = spotInfoRepository.findByContentTypeId(ContentTypeEnum.ACCOMMODATION.getCode(), pageable);
            spotInfoEntities.addAll(spotInfoRepository.findByContentTypeId(ContentTypeEnum.RESTAURANT.getCode(), pageable));
            spotInfoEntities.addAll(spotInfoRepository.findByContentTypeIdNotIn(Arrays.asList(ContentTypeEnum.ACCOMMODATION.getCode(), ContentTypeEnum.RESTAURANT.getCode()), pageable));
        } else if (townId == ALL) {
            spotInfoEntities = spotInfoRepository.findByContentTypeIdAndTown_TownPK_City_CityId(ContentTypeEnum.ACCOMMODATION.getCode(), cityId, pageable);
            spotInfoEntities.addAll(spotInfoRepository.findByContentTypeIdAndTown_TownPK_City_CityId(ContentTypeEnum.RESTAURANT.getCode(), cityId, pageable));
            spotInfoEntities.addAll(spotInfoRepository.findByContentTypeIdNotInAndTown_TownPK_City_CityId(Arrays.asList(ContentTypeEnum.ACCOMMODATION.getCode(), ContentTypeEnum.RESTAURANT.getCode()), cityId, pageable));
        } else {
            spotInfoEntities = spotInfoRepository.findByContentTypeIdAndTown_TownPK_City_CityIdAndTown_TownPK_TownId(ContentTypeEnum.ACCOMMODATION.getCode(), cityId, townId, pageable);
            spotInfoEntities.addAll(spotInfoRepository.findByContentTypeIdAndTown_TownPK_City_CityIdAndTown_TownPK_TownId(ContentTypeEnum.RESTAURANT.getCode(), cityId, townId, pageable));
            spotInfoEntities.addAll(spotInfoRepository.findByContentTypeIdNotInAndTown_TownPK_City_CityIdAndTown_TownPK_TownId(Arrays.asList(ContentTypeEnum.ACCOMMODATION.getCode(), ContentTypeEnum.RESTAURANT.getCode()), cityId, townId, pageable));
        }

        List<SpotDTO.SpotInfoDTO> spotInfoDtos = convertToDtoList(spotInfoEntities, userId);

        return new SpotDTO.SpotListDTO(spotInfoDtos.size() < SPOT_SEARCH_PER_PAGE /3, spotInfoDtos);
    }

    private List<SpotDTO.SpotInfoDTO> convertToDtoList(List<SpotInfoEntity> spotInfoEntities, long userId) {
        return spotInfoEntities.stream()
                .map(spotInfoEntity -> {
                    boolean isWishlist = wishListRepository.existsByUser_UserIdAndSpotInfo_SpotInfoId(userId, spotInfoEntity.getSpotInfoId());
                    return SpotDTO.SpotInfoDTO.convertToDto(spotInfoEntity, isWishlist);
                })
                .toList();
    }

/* 새 장소 등록 코드, 원본 작성자 퇴사 + 리뉴얼 제작성 하는걸로 자체 결정, 새로 쓸때 참고용으로 주석처리 해둠
    @Override
    @Transactional
    public SpotDTO.SpotAddResDTO createNewSpot(SpotAddReq spotAddReq, long userId) {
//        1. city 찾기
        String[] splitAddr = spotAddReq.getAddr1().split(" ");
        CityEntity cityEntity = getCityEntity(splitAddr[0]);
        TownEntity townEntity = null;

        Optional<TownEntity> townEntityOptional = townRepository.findByTownNameAndTownPK_City_CityId(splitAddr[1], cityEntity.getCityId());
        if (townEntityOptional.isPresent()) {
            townEntity = townEntityOptional.get();
        } else {
            townRepository.save(TownEntity.MakeNewSpotTownEntity(spotAddReq, splitAddr[1], new TownPK(townRepository.findMaxTownId() + 1, cityEntity)));
        }

        SpotInfoEntity spotInfo = SpotInfoEntity.MakeNewSpotEntity(spotAddReq, townEntity, MakeNewAddr(cityEntity.getCityName(), townEntity.getTownName(), splitAddr).toString());
        SpotInfoEntity newSpotInfo = spotInfoRepository.save(spotInfo);
        createNewDescrip(newSpotInfo, spotAddReq);

        if(spotAddReq.isAddPlanCheck()) {
            planService.addPlanSpot(spotAddReq.getPlanId(), newSpotInfo.getSpotInfoId(), userId);
        }

        return SpotDTO.SpotAddResDTO.EntityToDTO(spotInfo, spotAddReq.isAddPlanCheck());
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
    private void createNewDescrip(SpotInfoEntity spotInfoEntity, SpotAddReq spotAddReq) {
        SpotDescriptionEntity build = SpotDescriptionEntity.builder()
                .spotInfo(spotInfoEntity)
                .overview(spotAddReq.getOverview())
                .build();
        spotDescriptionRepository.save(build);
        createNewDetail(spotInfoEntity, spotAddReq);
    }

    //    @Override
    @Transactional
    public void createNewDetail(SpotInfoEntity spotInfoEntity, SpotAddReq spotAddReq) {

        String cat1 = null;
        String cat2 = null;
        String cat3 = null;
        switch (spotAddReq.getContentTypeId()) {
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
*/

    private List<BlogInfoResponse.Document> getBlogSearchInfo(String query, int page) {
        return kakaoService.getBlogInfo(query, "accuracy", page, BLOG_PER_PAGE).getDocuments();
    }
}
