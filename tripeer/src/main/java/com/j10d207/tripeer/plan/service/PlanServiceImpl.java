package com.j10d207.tripeer.plan.service;

import com.j10d207.tripeer.plan.dto.req.CoworkerInvitedReq;
import com.j10d207.tripeer.plan.dto.req.PlanCreateInfoReq;
import com.j10d207.tripeer.plan.dto.req.PlanDetailReq;
import com.j10d207.tripeer.plan.dto.req.TitleChangeReq;
import com.j10d207.tripeer.plan.dto.res.PlanDetailMainDTO;
import com.j10d207.tripeer.plan.dto.res.RootOptimizeDTO;
import com.j10d207.tripeer.plan.dto.res.SpotSearchResDTO;
import com.j10d207.tripeer.user.dto.res.UserDTO;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    /*
    플랜 생성 요청이 들어오면
    1. 플랜 엔티티 생성
    2. 생성한 유저를 플랜의 coworker(참가자)에 추가
    3. 플랜의 여행지 목록 생성
    4. 요청된 일자만큼 플랜 데이 생성
    5. 이메일 전송
     */
    @Override
    public PlanDetailMainDTO.CreateResultInfo createPlan(PlanCreateInfoReq planCreateInfoReq, long userId) {
        PlanDetailMainDTO.CreateResultInfo createResultInfo = PlanDetailMainDTO.CreateResultInfo.fromPlanCreateInfoReq(planCreateInfoReq);
        //플랜 생성 + 플랜번호 등록
        PlanEntity plan = planRepository.save(PlanEntity.fromDto(createResultInfo));
        createResultInfo.setPlanId(plan.getPlanId());
        //생성된 플랜을 가지기
        UserEntity user = userRepository.findByUserId(userId);
        coworkerRepository.save(CoworkerEntity.MakeCoworkerEntity(user, plan));

        List<PlanTownEntity> planTownEntityList = createResultInfo.getTownList().stream()
                .map(townDTO -> PlanTownEntity.ofDtoAndPlanEntity(townDTO, plan)).toList();
        planTownRepository.saveAll(planTownEntityList);

        int day = (int) ChronoUnit.DAYS.between(createResultInfo.getStartDay(), createResultInfo.getEndDay()) + 1;
        List<PlanDayEntity> planDayEntityList = new ArrayList<>();
        IntStream.range(0, day)
                .mapToObj(i -> PlanDayEntity.createEntity(createResultInfo, plan, i))
                .forEach(planDayEntityList::add);
        planDayRepository.saveAll(planDayEntityList);
        // 이메일 전송 스케쥴링
        planSchedulerService.schedulePlanTasks(plan);
        return createResultInfo;
    }

    //플랜 이름 변경
    @Override
    public void changeTitle(TitleChangeReq titleChangeReq, long userId) {
        PlanEntity plan = planRepository.findByPlanId(titleChangeReq.getPlanId());

        if(coworkerRepository.existsByPlan_PlanIdAndUser_UserId(titleChangeReq.getPlanId(), userId)) {
            plan.setTitle(titleChangeReq.getTitle());
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
        return coworkerList.stream()
                .map(coworker -> {
                    // 플랜 상세정보 가져오기
                    PlanEntity plan = planRepository.findByPlanId(coworker.getPlan().getPlanId());

                    // 플랜에서 선택한 타운 리스트 가져오기
                    List<PlanTownEntity> planTown = planTownRepository.findByPlan_PlanId(plan.getPlanId());

                    // PlanDetailMainDTO.MyPlan 객체 생성
                    return PlanDetailMainDTO.MyPlan.valueOfPlanPlanTownCoworkerEntity(
                            plan,
                            PlanTownEntity.getFirstImg(planTown),
                            planTown,
                            coworkerRepository.findByPlan_PlanId(plan.getPlanId()) // 플랜의 멤버 리스트 넣기
                    );
                })
                .collect(Collectors.toList());

    }

    //플랜 디테일 메인 조회
    /*
    생성된 Plan 에 접속하면 보이는 첫페이지에 필요한 정보 로딩
    동행자 목록, 선택한 도시들 목록이 로딩되어 전달
     */
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
        List<TownDTO> townDTOList = planTown.stream().map(TownDTO::fromPlanTownEntity).toList();

        return new PlanDetailMainDTO.MainPageInfo(planId, plan.getTitle(), townDTOList, coworkerEntityList.stream().map(UserDTO.Search::fromCoworkerEntity).toList());
    }

    //동행자 추가
    /*
    요청된 유저가 존재하는지 2중검증 후 등록이 완료되면 이메일 전송
     */
    @Override
    public void joinPlan(CoworkerInvitedReq coworkerInvitedReq, long userId) {
        //로그인 사용자가 소유하지 않은 플랜 접근시
        if(!coworkerRepository.existsByPlan_PlanIdAndUser_UserId(coworkerInvitedReq.getPlanId(), userId)) {
            throw new CustomException(ErrorCode.NOT_HAS_COWORKER);
        }
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        PlanEntity planEntity = planRepository.findById(coworkerInvitedReq.getPlanId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PLAN));
        UserEntity user = userRepository.findById(coworkerInvitedReq.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        if(coworkerRepository.findByUser_UserIdAndPlan_EndDateAfter(coworkerInvitedReq.getUserId(), LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1)).size() > 5) {
            throw new CustomException(ErrorCode.TOO_MANY_PLAN);
        }

        if(!coworkerRepository.existsByPlan_PlanIdAndUser_UserId(coworkerInvitedReq.getPlanId(), coworkerInvitedReq.getUserId())) {
            CoworkerEntity coworkerEntity = CoworkerEntity.MakeCoworkerEntity(user, PlanEntity.builder().planId(coworkerInvitedReq.getPlanId()).build());
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
        return coworkerList.stream().map(test -> PlanDetailMainDTO.PlanCoworker.fromCoworkerEntity(test, order.getAndIncrement())).toList();
    }


    //관광지 검색
    /*
    원본 JPA 내용
    SELECT * FROM spot_info where (제목이나 도시에 키워드 들어가있음) and (해당 플랜의 여행지중 하나) and (현재 선택된 관광지 유형)
     */
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
            spotSearchResDTOList.add(SpotSearchResDTO.fromSpotInfoEntity(spotInfoEntity, wishListRepository.existsByUser_UserIdAndSpotInfo_SpotInfoId(userId, spotInfoEntity.getSpotInfoId()), planBucketRepository.existsByPlan_PlanIdAndSpotInfo_SpotInfoId(planId, spotInfoEntity.getSpotInfoId())));
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
        planBucketRepository.save(PlanBucketEntity.createEntityOfId(planId, spotInfoId, userId));
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

    //즐겨찾기 조회
    @Override
    public List<SpotSearchResDTO> getWishList(long userId, long planId) {
        List<WishListEntity> wishList = wishListRepository.findByUser_UserId(userId);
        return  wishList.stream()
                .map(wishListEntity ->
                SpotSearchResDTO.fromWishListEntity
                        (wishListEntity,
                                planBucketRepository.existsByPlan_PlanIdAndSpotInfo_SpotInfoId(planId, wishListEntity.getSpotInfo().getSpotInfoId()))
                        )
                .toList();
    }

    //플랜 디테일 저장
    @Override
    public void addPlanDetail(PlanDetailReq planDetailReq) {
        planDetailRepository.save(PlanDetailEntity.fromReq(planDetailReq));
    }

    //플랜 디테일 전체 조회
    @Override
    public Map<Integer, List<PlanDetailMainDTO.PlanSpotDetail>> getAllPlanDetail(long planId) {
        PlanEntity plan = planRepository.findByPlanId(planId);
        return IntStream.range(0, (int) ChronoUnit.DAYS.between(plan.getStartDate(), plan.getEndDate()) + 1)
                .boxed()
                .collect(Collectors.toMap(
                        i -> i + 1,
                        i -> {
                            long planDayId = planDayRepository.findByPlan_PlanIdAndDay(planId, plan.getStartDate().plusDays(i)).getPlanDayId();
                            List<PlanDetailEntity> planDetailEntityList = planDetailRepository.findByPlanDay_PlanDayId(planDayId, Sort.by(Sort.Direction.ASC, "step"));
                            return planDetailEntityList.stream().map(PlanDetailMainDTO.PlanSpotDetail::fromEntity).toList();
                        }
                ));
    }

    //플랜 나의 정보 조회(기존 내정보 + 나의 coworker 에서의 순서)
    @Override
    public PlanDetailMainDTO.PlanCoworker getPlanMyInfo(long planId, long userId) {
        //요청된 플랜의 동행자 목록 조회
        List<CoworkerEntity> coworkerList = coworkerRepository.findByPlan_PlanId(planId);

        return IntStream.range(0, coworkerList.size())
                .filter(i -> userId == coworkerList.get(i).getUser().getUserId()) // userId가 일치하는 인덱스를 필터링
                .mapToObj(i -> PlanDetailMainDTO.PlanCoworker.fromCoworkerEntity(coworkerList.get(i), i)) // 인덱스와 함께 객체로 변환
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_HAS_COWORKER));
    }

    /*
    하단 메소드는 TMap Branch 를 통해 따로 PR 리뷰 요청 예정
     */

    //목적지간 최단 루트 계산
    public RootOptimizeDTO getShortTime(RootOptimizeDTO rootOptimizeDTO) {
        int infoSize = rootOptimizeDTO.getPlaceList().size();
        if( infoSize < 2) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_INFO);
        } else if(infoSize > 2) {
            throw new CustomException(ErrorCode.TOO_MANY_INFO);
        }
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
            StringBuilder time = new StringBuilder();
            switch (result.getStatus()) {
                case 0:
                    if(result.getTime()/60 > 0) {
                        time.append(result.getTime()/60).append("시간 ");
                    }
                    time.append(result.getTime()%60).append("분");

                    timeList.add(new String[]{time.toString(), String.valueOf(rootOptimizeDTO.getOption()) });
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

        PublicRootDTO publicRoot = PublicRootDTO.fromJson(infoObject);


        List<PublicRootDTO> rootList = new ArrayList<>();
        if(rootOptimizeDTO.getPublicRootList() != null) {
            rootList = rootOptimizeDTO.getPublicRootList();
        }
        rootList.add(publicRoot);
        rootOptimizeDTO.setPublicRootList(rootList);

        return rootOptimizeDTO;
    }

}
