package com.j10d207.tripeer.place.service;

import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.kakao.db.entity.BlogInfoResponse;
import com.j10d207.tripeer.kakao.service.KakaoService;
import com.j10d207.tripeer.place.db.ContentTypeEnum;
import com.j10d207.tripeer.place.db.entity.*;
import com.j10d207.tripeer.place.db.repository.*;
import com.j10d207.tripeer.place.db.repository.additional.AdditionalBaseRepository;
import com.j10d207.tripeer.place.dto.req.SpotAddReq;
import com.j10d207.tripeer.place.dto.res.AdditionalDto;
import com.j10d207.tripeer.place.dto.res.ReviewDto;
import com.j10d207.tripeer.place.dto.res.SpotDTO;
import com.j10d207.tripeer.place.dto.res.SpotDetailPageDto;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import com.j10d207.tripeer.user.db.repository.UserRepository;
import com.j10d207.tripeer.user.db.repository.WishListRepository;

import jakarta.transaction.Transactional;
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
    private final CityRepository cityRepository;
    private final TownRepository townRepository;
    private final SpotDetailRepository spotDetailRepository;
    private final UserRepository userRepository;


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
        Set<Integer> wishList = wishListRepository.findAllSpotInfoIdsByUserId(userId);
        spotDetailPageDto.setLike(wishList.contains(spotInfoId));
        spotDetailPageDto.setAdditionalInfo(AdditionalDto.from(additionalBaseRepository.findBySpotInfo(spotInfoEntity)));

        SpotCollectionEntity spotCollection = spotCollectionRepository.findBySpotInfoId(spotInfoEntity.getSpotInfoId());
        if (spotCollection != null) {
            spotDetailPageDto.setSimilarSpotList(spotCollection.getSimSpotIdList().stream().map(
                spotInfoRepository::findBySpotInfoId).map(el -> SpotDTO.SpotInfoDTO.convertToDto(el, wishList.contains(el.getSpotInfoId()))).toList());
            spotDetailPageDto.setNearSpotList(spotCollection.getNearSpotIdList().stream().map(
                spotInfoRepository::findBySpotInfoId).map(el -> SpotDTO.SpotInfoDTO.convertToDto(el, wishList.contains(el.getSpotInfoId()))).toList());
        } else {
            spotDetailPageDto.setSimilarSpotList(Collections.emptyList());
            spotDetailPageDto.setNearSpotList(Collections.emptyList());
        }

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

    // 홈에서 사용할 검색 ( 오직 키워드만 가지고 검색 )
    @Override
    public SpotDTO.SpotListDTO getHomeSearch(String keyword, int page, long userId) {
        Pageable pageable = PageRequest.of(page-1, SPOT_SEARCH_PER_PAGE);
        Page<SpotInfoEntity> spotInfoEntities = spotInfoRepository.searchSpotsByKeyword(keyword, pageable);
        List<SpotDTO.SpotInfoDTO> spotInfoDTOList = convertToDtoList(spotInfoEntities.stream().toList(), userId);
        return new SpotDTO.SpotListDTO(spotInfoEntities.isLast(), spotInfoDTOList);
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
        Set<Integer> wishList = wishListRepository.findAllSpotInfoIdsByUserId(userId);
        return spotInfoEntities.stream()
                .map(spotInfoEntity -> {
                    return SpotDTO.SpotInfoDTO.convertToDto(spotInfoEntity, wishList.contains(spotInfoEntity.getSpotInfoId()));
                })
                .toList();
    }

    @Override
    @Transactional
    public SpotDTO.SpotAddResDTO createNewSpot(SpotAddReq spotAddReq, long userId) {
        //중복 장소 검증
        checkNearSpot(spotAddReq.getTitle(), spotAddReq.getLatitude(), spotAddReq.getLongitude());
//        1. city 찾기
        String[] splitAddr = spotAddReq.getAddr().split(" ");
        CityEntity cityEntity = getCityEntity(splitAddr[0]);
        TownEntity townEntity = null;

        Optional<TownEntity> townEntityOptional = townRepository.findByTownNameAndTownPK_City_CityId(splitAddr[1], cityEntity.getCityId());
        if (townEntityOptional.isPresent()) {
            townEntity = townEntityOptional.get();
        } else {
            townRepository.save(TownEntity.ofSpotAddReq(spotAddReq, splitAddr[1], new TownPK(townRepository.findMaxTownId() + 1, cityEntity)));
        }

        SpotInfoEntity spotInfo = SpotInfoEntity.ofReq(spotAddReq, townEntity, MakeNewAddr(cityEntity.getCityName(), townEntity.getTownName(), splitAddr).toString());
        SpotInfoEntity newSpotInfo = spotInfoRepository.save(spotInfo);
        createNewDescrip(newSpotInfo, spotAddReq);


        UserEntity user = userRepository.findByUserId(userId);
        return SpotDTO.SpotAddResDTO.ofEntity(spotInfo, user, spotAddReq.getOrder());
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
                .overview(spotAddReq.getDescription())
                .build();
        spotDescriptionRepository.save(build);
        createNewDetail(spotInfoEntity, spotAddReq);
    }

    //    @Override
    @Transactional
    private void createNewDetail(SpotInfoEntity spotInfoEntity, SpotAddReq spotAddReq) {

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

        spotDetailRepository.save(SpotDetailEntity.ofInfoEntity(spotInfoEntity, cat1, cat2, cat3));
    }

    private void checkNearSpot(String title, double latitude, double longitude) {
        double nearRange = 0.001;
        if (spotInfoRepository.searchNearSpot(title, latitude-nearRange, longitude+nearRange,
                longitude-nearRange,longitude+nearRange).isPresent() ) {
            throw new CustomException(ErrorCode.FOUND_SPOT);
        } else {
            return;
        }
    }

    private List<BlogInfoResponse.Document> getBlogSearchInfo(String query, int page) {
        return kakaoService.getBlogInfo(query, "accuracy", page, BLOG_PER_PAGE).getDocuments();
    }
}
