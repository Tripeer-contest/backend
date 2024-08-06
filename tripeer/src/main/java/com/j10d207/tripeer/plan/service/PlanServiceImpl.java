package com.j10d207.tripeer.plan.service;

import com.j10d207.tripeer.plan.db.vo.CoworkerInvitedVO;
import com.j10d207.tripeer.plan.db.vo.PlanDetailVO;
import com.j10d207.tripeer.plan.db.vo.TitleChangeVO;
import com.j10d207.tripeer.tmap.db.dto.PublicRootDTO;
import com.j10d207.tripeer.user.db.dto.UserDTO;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.j10d207.tripeer.email.db.dto.EmailDTO;
import com.j10d207.tripeer.email.service.EmailService;
import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.kakao.service.KakaoService;
import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import com.j10d207.tripeer.place.db.repository.SpotInfoRepository;
import com.j10d207.tripeer.plan.db.dto.*;
import com.j10d207.tripeer.plan.db.entity.*;
import com.j10d207.tripeer.plan.db.repository.*;
import com.j10d207.tripeer.tmap.db.dto.CoordinateDTO;
import com.j10d207.tripeer.tmap.db.dto.RootInfoDTO;
import com.j10d207.tripeer.tmap.service.FindRoot;
import com.j10d207.tripeer.tmap.service.TMapService;
import com.j10d207.tripeer.user.db.entity.CoworkerEntity;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import com.j10d207.tripeer.user.db.entity.WishListEntity;
import com.j10d207.tripeer.user.db.repository.CoworkerRepository;
import com.j10d207.tripeer.user.db.repository.UserRepository;
import com.j10d207.tripeer.user.db.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanServiceImpl implements PlanService {

    private final UserRepository userRepository;
    private final CoworkerRepository coworkerRepository;
    private final WishListRepository wishListRepository;

    private final PlanRepository planRepository;
    private final PlanTownRepository planTownRepository;
    private final PlanDayRepository planDayRepository;
    private final PlanBucketRepository planBucketRepository;
    private final PlanSchedulerService planSchedulerService;

    private final SpotInfoRepository spotInfoRepository;
    private final PlanDetailRepository planDetailRepository;

    private final TMapService tMapService;

    private final KakaoService kakaoService;

    private final EmailService emailService;

    //플랜 생성
    @Override
    public PlanDetailMainDTO.CreateResultInfo createPlan(PlanDetailMainDTO.CreateResultInfo createResultInfo, long userId) {
        //플랜 생성 + 플랜번호 등록
        PlanEntity plan = planRepository.save(PlanEntity.DTOToEntity(createResultInfo));
        createResultInfo.setPlanId(plan.getPlanId());
        //생성된 플랜을 가지기
        UserEntity user = userRepository.findByUserId(userId);
        coworkerRepository.save(CoworkerEntity.MakeCoworkerEntity(user, plan));

        for (TownDTO townDTO : createResultInfo.getTownList()) {
            //request 기반으로 townEntity 받아오기
            planTownRepository.save(PlanTownEntity.TownDTOToEntity(townDTO, plan));
        }
        int day = (int) ChronoUnit.DAYS.between(createResultInfo.getStartDay(), createResultInfo.getEndDay()) + 1;
        for (int i = 0; i < day; i++) {
            planDayRepository.save(PlanDayEntity.MakeDayEntity(createResultInfo, plan, i));
        }
        // 이메일 전송 스케쥴링
        planSchedulerService.schedulePlanTasks(plan);
        return createResultInfo;
    }

    //플랜 이름 변경
    @Override
    public void changeTitle(TitleChangeVO titleChangeVO, long userId) {
        PlanEntity plan = planRepository.findByPlanId(titleChangeVO.getPlanId());

        if(coworkerRepository.existsByPlan_PlanIdAndUser_UserId(titleChangeVO.getPlanId(), userId)) {
            plan.setTitle(titleChangeVO.getTitle());
            planRepository.save(plan);
        } else {
            // 토큰과 소유자가 일치하지 않음
            throw new CustomException(ErrorCode.USER_NOT_CORRESPOND);
        }

    }

    //플랜 탈퇴
    @Override
    public void planOut(long planId, long userId) {
        Optional<CoworkerEntity> coworkerOptional = coworkerRepository.findByPlan_PlanIdAndUser_UserId(planId, userId);
        if(coworkerOptional.isPresent()) {
            coworkerRepository.delete(coworkerOptional.get());
        } else {
            throw new CustomException(ErrorCode.NOT_HAS_COWORKER);
        }

    }

    //내 플랜 리스트 조회
    @Override
    public List<PlanDetailMainDTO.MyPlan> planList(long userId) {
        // 사용자가 소유중인 플랜의 리스트 목록을 가져옴
        List<CoworkerEntity> coworkerList = coworkerRepository.findByUser_UserIdAndPlan_EndDateAfter(userId, LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1));

        // 반환리스트를 담아줄 DTO 생성
        List<PlanDetailMainDTO.MyPlan> myPlans = new ArrayList<>();
        if(coworkerList.isEmpty()) {
            return myPlans;
        }

        // 내가 가진 플랜을 하나씩 조회
        for (CoworkerEntity coworker : coworkerList) {
            // 플랜 상세정보 가져오기
            PlanEntity plan = planRepository.findByPlanId(coworker.getPlan().getPlanId());
            // 플랜에서 선택한 타운 리스트 가져오기
            List<PlanTownEntity> planTown = planTownRepository.findByPlan_PlanId(plan.getPlanId());
            PlanDetailMainDTO.MyPlan myPlan = PlanDetailMainDTO.MyPlan.EntityToDTO(plan, PlanTownEntity.getFirstImg(planTown), planTown, coworkerRepository.findByPlan_PlanId(plan.getPlanId())); // 플랜의 멤버 리스트 넣기
            myPlans.add(myPlan);
        }

        return myPlans;
    }

    //플랜 디테일 메인 조회
    @Override
    public PlanDetailMainDTO.MainPageInfo getPlanDetailMain(long planId, long userId) {
        PlanEntity plan = planRepository.findByPlanId(planId);
        //로그인 사용자가 소유하지 않은 플랜 접근시
        if(!coworkerRepository.existsByPlan_PlanIdAndUser_UserId(planId, userId)) {
            throw new CustomException(ErrorCode.NOT_HAS_COWORKER);
        }
        List<PlanTownEntity> planTown = planTownRepository.findByPlan_PlanId(plan.getPlanId());
        List<CoworkerEntity> coworkerEntityList = coworkerRepository.findByPlan_PlanId(plan.getPlanId());

        //선택한 도시 목록 구성
        List<TownDTO> townDTOList = planTown.stream().map(TownDTO::EntityToDTO).toList();

        return new PlanDetailMainDTO.MainPageInfo(planId, plan.getTitle(), townDTOList, coworkerEntityList.stream().map(UserDTO.Search::CoworkerEntityToDTO).toList());
    }

    //동행자 추가
    @Override
    public void joinPlan(CoworkerInvitedVO coworkerInvitedVO, long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        PlanEntity planEntity = planRepository.findById(coworkerInvitedVO.getPlanId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PLAN));
        UserEntity user = UserEntity.builder().userId(coworkerInvitedVO.getUserId()).build();

        if(coworkerRepository.findByUser_UserIdAndPlan_EndDateAfter(coworkerInvitedVO.getUserId(), LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1)).size() > 5) {
            throw new CustomException(ErrorCode.TOO_MANY_PLAN);
        }

        if(!coworkerRepository.existsByPlan_PlanIdAndUser_UserId(coworkerInvitedVO.getPlanId(), coworkerInvitedVO.getUserId())) {
            CoworkerEntity coworkerEntity = CoworkerEntity.MakeCoworkerEntity(user, PlanEntity.builder().planId(coworkerInvitedVO.getPlanId()).build());
            coworkerRepository.save(coworkerEntity);

            emailService.sendEmail(EmailDTO.MakeInvitedEmail(planEntity.getTitle(), userEntity.getNickname(), user.getUserId()));
        } else {
            throw new CustomException(ErrorCode.DUPLICATE_USER);
        }
    }

    //동행자 조회
    @Override
    public List<PlanDetailMainDTO.PlanCoworker> getCoworker(long planId) {
        //요청된 플랜의 동행자 목록 조회
        List<CoworkerEntity> coworkerList = coworkerRepository.findByPlan_PlanId(planId);
        //DTO로 변환
        AtomicInteger order = new AtomicInteger();
        return coworkerList.stream().map(test -> PlanDetailMainDTO.PlanCoworker.CoworkerToDTO(test, order.getAndIncrement())).toList();
    }


    //관광지 검색
    @Override
    public List<SpotSearchResDTO> getSpotSearch(long planId, String keyword, int page, int sortType, long userId) {
        Specification<SpotInfoEntity> spotInfoSpec = Specification.where(null);
        List<PlanTownEntity> planTownList = planTownRepository.findByPlan_PlanId(planId);
        Pageable pageable = PageRequest.of(page, 10);

        Specification<SpotInfoEntity> titleSpec = Specification.where(null);
        titleSpec = titleSpec.or((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "%" + keyword + "%"));
        titleSpec = titleSpec.or((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("addr1"), "%" + keyword + "%"));

        Specification<SpotInfoEntity> townSpec = Specification.where(null);
        for (PlanTownEntity planTownEntity : planTownList) {
            Specification<SpotInfoEntity> cityAndTownSpec = Specification.where(null);
            if ( planTownEntity.getCityOnly() == null ) {
                int cityId = planTownEntity.getTown().getTownPK().getCity().getCityId();
                int townId = planTownEntity.getTown().getTownPK().getTownId();
                cityAndTownSpec = cityAndTownSpec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("town").join("townPK").join("city").get("cityId"), cityId));
                cityAndTownSpec = cityAndTownSpec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("town").join("townPK").get("townId"), townId));
            } else {
                int cityId = planTownEntity.getCityOnly().getCityId();
                cityAndTownSpec = cityAndTownSpec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("town").join("townPK").join("city").get("cityId"), cityId));
            }
            townSpec = townSpec.or(cityAndTownSpec);
        }

        Specification<SpotInfoEntity> contentTypeSpec = Specification.where(null);
        if( sortType == 2 ) {
            contentTypeSpec = contentTypeSpec.or((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("contentTypeId"), 12));
            contentTypeSpec = contentTypeSpec.or((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("contentTypeId"), 14));
            contentTypeSpec = contentTypeSpec.or((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("contentTypeId"), 15));
            contentTypeSpec = contentTypeSpec.or((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("contentTypeId"), 25));
            contentTypeSpec = contentTypeSpec.or((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("contentTypeId"), 28));
            contentTypeSpec = contentTypeSpec.or((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("contentTypeId"), 38));
        } else if ( sortType == 3 ) {
            contentTypeSpec = contentTypeSpec.or((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("contentTypeId"), 32));
        } else if ( sortType == 4 ) {
            contentTypeSpec = contentTypeSpec.or((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("contentTypeId"), 39));
        }
        spotInfoSpec = spotInfoSpec.and(titleSpec);
        spotInfoSpec = spotInfoSpec.and(contentTypeSpec);
        spotInfoSpec = spotInfoSpec.and(townSpec);
        List<SpotInfoEntity> spotInfoList = spotInfoRepository.findAll(spotInfoSpec, pageable);
        if(spotInfoList.isEmpty() && page == 0) {
            throw new CustomException(ErrorCode.SEARCH_NULL);
        } else if (spotInfoList.isEmpty() && page > 1) {
            throw new CustomException(ErrorCode.SCROLL_END);
        }


        List<SpotSearchResDTO> spotSearchResDTOList = new ArrayList<>();
        for (SpotInfoEntity spotInfoEntity : spotInfoList) {
            spotSearchResDTOList.add(SpotSearchResDTO.SpotInfoEntityToDTO(spotInfoEntity, wishListRepository.existsByUser_UserIdAndSpotInfo_SpotInfoId(userId, spotInfoEntity.getSpotInfoId()), planBucketRepository.existsByPlan_PlanIdAndSpotInfo_SpotInfoId(planId, spotInfoEntity.getSpotInfoId())));
        }

        return spotSearchResDTOList;
    }

    //플랜 버킷 관광지 추가
    @Override
    public void addPlanSpot(long planId, int spotInfoId, long userId) {
        if(planBucketRepository.existsByPlan_PlanIdAndSpotInfo_SpotInfoId(planId, spotInfoId)) {
            throw new CustomException(ErrorCode.HAS_BUCKET);
        }
        if(!(coworkerRepository.existsByPlan_PlanIdAndUser_UserId(planId, userId))) {
            throw new CustomException(ErrorCode.USER_NOT_CORRESPOND);
        }
        planBucketRepository.save(PlanBucketEntity.MakePlanBucketEntity(planId, spotInfoId, userId));
    }

    //플랜 버킷 관광지 삭제
    @Override
    public void delPlanSpot(long planId, int spotInfoId, long userId) {
        Optional<PlanBucketEntity> planBucket = planBucketRepository.findByPlan_PlanIdAndSpotInfo_SpotInfoId(planId, spotInfoId);
        if (planBucket.isPresent()){
            PlanBucketEntity planBucketEntity = planBucket.get();
            if (coworkerRepository.existsByPlan_PlanIdAndUser_UserId(planBucketEntity.getPlan().getPlanId(), userId)) {
                planBucketRepository.delete(planBucketEntity);
            } else {
                // 로그인된 사용자가 가지고 있지 않은 변경
                throw new CustomException(ErrorCode.USER_NOT_CORRESPOND);
            }

        } else {
            // 요청된 장소를 소유하지 않음
            throw new CustomException(ErrorCode.SPOT_NOT_FOUND);
        }
    }

    //즐겨찾기 추가
    @Override
    public void addWishList(int spotInfoId, long userId) {
        Optional<WishListEntity> optionalWishList = wishListRepository.findBySpotInfo_SpotInfoIdAndUser_UserId(spotInfoId, userId);
        if (optionalWishList.isPresent()) {
            wishListRepository.delete(optionalWishList.get());
        } else {
            wishListRepository.save(WishListEntity.MakeWishListEntity(spotInfoId, userId));
        }
    }

    //즐겨찾기 조회
    @Override
    public List<SpotSearchResDTO> getWishList(long userId, long planId) {
        List<WishListEntity> wishList = wishListRepository.findByUser_UserId(userId);
        return  wishList.stream()
                .map(wishListEntity ->
                SpotSearchResDTO.WishEntityToDTO
                        (wishListEntity,
                                planBucketRepository.existsByPlan_PlanIdAndSpotInfo_SpotInfoId(planId, wishListEntity.getSpotInfo().getSpotInfoId()))
                        )
                .toList();
    }

    //플랜 디테일 저장
    @Override
    public void addPlanDetail(PlanDetailVO planDetailVO) {
        planDetailRepository.save(PlanDetailEntity.VOToEntity(planDetailVO));
    }

    //플랜 디테일 전체 조회
    @Override
    public Map<Integer, List<PlanDetailMainDTO.PlanSpotDetail>> getAllPlanDetail(long planId) {
        Map<Integer, List<PlanDetailMainDTO.PlanSpotDetail>> planSpotDetailMap = new HashMap<>();
        //조회할 플랜을 가져옴
        PlanEntity plan = planRepository.findByPlanId(planId);
        //시작날짜, 끝날짜를 이용해서 몇일 여행인지 계산
        int day = (int) ChronoUnit.DAYS.between(plan.getStartDate(), plan.getEndDate()) + 1;

        //여행알 일자만큼 반복, 각 일자별 디테일을 뽑아오기 위해
        for (int i = 0; i < day; i++) {
            // N일차 플랜의 id를 찾아옴
            long planDayId = planDayRepository.findByPlan_PlanIdAndDay(planId, plan.getStartDate().plusDays(i)).getPlanDayId();
            // 얻으려는 일차의 플랜을 step 순서로 정렬
            List<PlanDetailEntity> planDetailEntityList = planDetailRepository.findByPlanDay_PlanDayId(planDayId, Sort.by(Sort.Direction.ASC, "step"));

            List<PlanDetailMainDTO.PlanSpotDetail> planSpotDetailList = planDetailEntityList.stream().map(PlanDetailMainDTO.PlanSpotDetail::EntityToDTO).toList();
            planSpotDetailMap.put(i+1, planSpotDetailList);
        }
        return planSpotDetailMap;
    }

    //플랜 나의 정보 조회(기존 내정보 + 나의 coworker에서의 순서)
    @Override
    public PlanDetailMainDTO.PlanCoworker getPlanMyinfo(long planId, long userId) {
        //요청된 플랜의 동행자 목록 조회
        List<CoworkerEntity> coworkerList = coworkerRepository.findByPlan_PlanId(planId);
        int order = -1;
        for (CoworkerEntity coworker : coworkerList) {
            order++;
            if(userId != coworker.getUser().getUserId()) continue;
            return PlanDetailMainDTO.PlanCoworker.CoworkerToDTO(coworker, order);
        }
        throw new CustomException(ErrorCode.NOT_HAS_COWORKER);
    }


    //목적지간 최단 루트 계산
    public RootOptimizeDTO getShortTime(RootOptimizeDTO rootOptimizeDTO) {
        // 장소 갯수 카운트 AtoB 이므로 2개가 아니면 throw
        int infoSize = rootOptimizeDTO.getPlaceList().size();
        if( infoSize < 2) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_INFO);
        } else if(infoSize > 2) {
            throw new CustomException(ErrorCode.TOO_MANY_INFO);
        }
        // option 0이면 자차(택시) -> 카카오
        if (rootOptimizeDTO.getOption() == 0) {
            int resultTime = kakaoService.getDirections(rootOptimizeDTO.getPlaceList().getFirst().getLongitude(),
                    rootOptimizeDTO.getPlaceList().getFirst().getLatitude(),
                    rootOptimizeDTO.getPlaceList().getLast().getLongitude(),
                    rootOptimizeDTO.getPlaceList().getLast().getLatitude());
            StringBuilder rootInfoBuilder = new StringBuilder();
            List<String[]> timeList = new ArrayList<>();
            if( resultTime == 99999 ) {
                rootOptimizeDTO.setOption(400);
                rootInfoBuilder.append("경로를 찾을 수 없습니다.");
                timeList.add(new String[] {rootInfoBuilder.toString(), "2" } );
                rootOptimizeDTO.setSpotTime(timeList);
                return rootOptimizeDTO;
            }
            if(resultTime/60 > 0) {
                rootInfoBuilder.append(resultTime/60).append("시간 ");
            }
            rootInfoBuilder.append(resultTime%60).append("분");
            timeList.add(new String[] {rootInfoBuilder.toString(), String.valueOf(rootOptimizeDTO.getOption()) } );
            rootOptimizeDTO.setSpotTime(timeList);
            return rootOptimizeDTO;
        }
        else if (rootOptimizeDTO.getOption() == 1) {
            RootInfoDTO baseInfo = RootInfoDTO.builder()
                    .startTitle(rootOptimizeDTO.getPlaceList().getFirst().getTitle())
                    .endTitle(rootOptimizeDTO.getPlaceList().getLast().getTitle())
                    .build();

            RootInfoDTO result = tMapService.getPublicTime(rootOptimizeDTO.getPlaceList().getFirst().getLongitude(),
                    rootOptimizeDTO.getPlaceList().getFirst().getLatitude(),
                    rootOptimizeDTO.getPlaceList().getLast().getLongitude(),
                    rootOptimizeDTO.getPlaceList().getLast().getLatitude(), baseInfo);
            List<String[]> timeList = new ArrayList<>();

            if (result.getStatus() == 0) {

                timeList.add(new String[]{result.timeToString(), String.valueOf(rootOptimizeDTO.getOption()) });
                rootOptimizeDTO.setSpotTime(timeList);

                JsonElement rootInfo = result.getRootInfo();
                if (result.getPublicRoot() != null ) {
                    List<PublicRootDTO> publicRootDTOList = new ArrayList<>();
                    publicRootDTOList.add(result.getPublicRoot());
                    rootOptimizeDTO.setPublicRootList(publicRootDTOList);
                    return rootOptimizeDTO;
                } else {
                    return MakeRootInfo(rootOptimizeDTO, rootInfo);
                }
            } else {
                timeList = tMapApiErrorCodeFilter(result.getStatus());
            }
            rootOptimizeDTO.setOption(result.getStatus());
            rootOptimizeDTO.setSpotTime(timeList);
            rootOptimizeDTO.setOption(result.getStatus());
            return rootOptimizeDTO;
        }
        else {
            throw new CustomException(ErrorCode.ROOT_API_ERROR);
        }

    }

    //플랜 최단거리 조정
    @Override
    public RootOptimizeDTO getOptimizingTime(RootOptimizeDTO rootOptimizeDTO) throws IOException {
        if(rootOptimizeDTO.getPlaceList().size() < 3) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_INFO);
        }
        List<CoordinateDTO> coordinateDTOList = new ArrayList<>();
        // 전달 받은 정보를 기반으로 좌표 리스트 생성
        List<RootOptimizeDTO.place> placeList = rootOptimizeDTO.getPlaceList();
        for (RootOptimizeDTO.place place : placeList) {
            CoordinateDTO coordinateDTO = CoordinateDTO.builder()
                    .title(place.getTitle())
                    .latitude(place.getLatitude())
                    .longitude(place.getLongitude())
                    .build();
            coordinateDTOList.add(coordinateDTO);
        }

        FindRoot root = null;
        RootOptimizeDTO result = new RootOptimizeDTO();
        // 자동차
        if ( rootOptimizeDTO.getOption() == 0 ) {
            root = kakaoService.getOptimizingTime(coordinateDTOList);
            result.setOption(0);
        }
        // 대중교통
        else if ( rootOptimizeDTO.getOption() == 1 ) {
            result.setOption(1);
            root = tMapService.getOptimizingTime(coordinateDTOList);
        } else {
            result.setOption(-1);
        }


        List<RootOptimizeDTO.place> newPlaceList = new ArrayList<>();
        List<String[]> newSpotTimeList = new ArrayList<>();

        if (root != null) {

            int j = 0;
            for(Integer i : root.getResultNumbers()) {
                StringBuilder sb = new StringBuilder();
                if( root.getRootTime()[j]/60 != 0 ) {
                    sb.append(root.getRootTime()[j]/60).append("시간 ");
                }
                sb.append(root.getRootTime()[j++]%60).append("분");

                int nowStatus = j == root.getResultNumbers().size() ? rootOptimizeDTO.getOption() :root.getTimeTable()[i][root.getResultNumbers().get(j)].getStatus();
                if( nowStatus > 10 & nowStatus < 15) {
                    newSpotTimeList.add(new String[]{sb.toString(), "0" });
                } else {
                    newSpotTimeList.add(new String[]{sb.toString(), String.valueOf(rootOptimizeDTO.getOption()) });
                }
                RootOptimizeDTO.place newPlace = rootOptimizeDTO.getPlaceList().get(i);
//                if (result.getOption() == 1) {
//                    newPlace.setMovingRoot(j == root.getResultNumbers().size() ? "null" : root.getTimeTable()[i][root.getResultNumbers().get(j)].getRootInfo().toString());
//                }
                JsonElement info = j == root.getResultNumbers().size() ? null : root.getTimeTable()[i][root.getResultNumbers().get(j)].getRootInfo();
                if( j != root.getResultNumbers().size() ) {
                    if (root.getTimeTable()[i][root.getResultNumbers().get(j)].getPublicRoot() != null) {
                        List<PublicRootDTO> publicRootDTOList = new ArrayList<>();
                        publicRootDTOList.add(root.getTimeTable()[i][root.getResultNumbers().get(j)].getPublicRoot());
                        rootOptimizeDTO.setPublicRootList(publicRootDTOList);
                    } else {
                        rootOptimizeDTO = MakeRootInfo(rootOptimizeDTO, info);
                    }
                }
                newPlaceList.add(newPlace);
            }
            result.setPlaceList(newPlaceList);
            result.setSpotTime(newSpotTimeList);
            result.setPublicRootList(rootOptimizeDTO.getPublicRootList());

            return result;
        }


        return null;
    }

    private RootOptimizeDTO MakeRootInfo(RootOptimizeDTO rootOptimizeDTO, JsonElement rootInfo) {
        if(rootInfo == null) {
            List<PublicRootDTO> rootList = new ArrayList<>();
            if(rootOptimizeDTO.getPublicRootList() != null) {
                rootList = rootOptimizeDTO.getPublicRootList();
            }
            rootList.add(null);
            rootOptimizeDTO.setPublicRootList(rootList);
            return rootOptimizeDTO;
        }
        JsonObject infoObject = rootInfo.getAsJsonObject();

        PublicRootDTO publicRoot = PublicRootDTO.JsonToDTO(infoObject);


        List<PublicRootDTO> rootList = new ArrayList<>();
        if(rootOptimizeDTO.getPublicRootList() != null) {
            rootList = rootOptimizeDTO.getPublicRootList();
        }
        rootList.add(publicRoot);
        rootOptimizeDTO.setPublicRootList(rootList);

        return rootOptimizeDTO;
    }

    private List<String[]> tMapApiErrorCodeFilter (int code) {
        List<String[]> timeList = new ArrayList<>();
        StringBuilder time = new StringBuilder();
        switch (code) {
//            case 0:
//                if(result.getTime()/60 > 0) {
//                    time.append(result.getTime()/60).append("시간 ");
//                }
//                time.append(result.getTime()%60).append("분");
//
//                timeList.add(new String[]{time.toString(), String.valueOf(rootOptimizeDTO.getOption()) });
//                rootOptimizeDTO.setSpotTime(timeList);
//
//                JsonElement rootInfo = result.getRootInfo();
//                if (result.getPublicRoot() != null ) {
//                    List<PublicRootDTO> publicRootDTOList = new ArrayList<>();
//                    publicRootDTOList.add(result.getPublicRoot());
//                    rootOptimizeDTO.setPublicRootList(publicRootDTOList);
//                    return rootOptimizeDTO;
//                } else {
//                    return MakeRootInfo(rootOptimizeDTO, rootInfo);
//                }
            //11 -출발지/도착지 간 거리가 가까워서 탐색된 경로 없음
            //12 -출발지에서 검색된 정류장이 없어서 탐색된 경로 없음
            //13 -도착지에서 검색된 정류장이 없어서 탐색된 경로 없음
            //14 -출발지/도착지 간 탐색된 대중교통 경로가 없음
            case 11:
            case 411:
                time.append("출발지/도착지 간 거리가 가까워서 탐색된 경로가 없습니다.");
                timeList.add(new String[]{time.toString(), "2" });
                break;
            case 12:
            case 412:
                time.append("출발지에서 검색된 정류장이 없어 탐색된 경로가 없습니다.");
                timeList.add(new String[]{time.toString(), "2" });
                break;
            case 13:
            case 413:
                time.append("도착지에서 검색된 정류장이 없어 탐색된 경로가 없습니다.");
                timeList.add(new String[]{time.toString(), "2" });
                break;
            case 14:
            case 414:
                time.append("출발지/도착지 간 탐색된 대중교통 경로가 없어 탐색된 경로가 없습니다.");
                timeList.add(new String[]{time.toString(), "2" });
                break;
            default:
                throw new CustomException(ErrorCode.ROOT_API_ERROR);
        }

        return timeList;
    }

}
