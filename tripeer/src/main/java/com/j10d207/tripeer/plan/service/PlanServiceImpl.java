package com.j10d207.tripeer.plan.service;

import com.j10d207.tripeer.common.CommonMethod;
import com.j10d207.tripeer.place.db.ContentTypeEnum;
import com.j10d207.tripeer.place.db.entity.CityEntity;
import com.j10d207.tripeer.place.db.entity.ElasticSpotEntity;
import com.j10d207.tripeer.place.db.entity.TownEntity;
import com.j10d207.tripeer.place.db.repository.CityRepository;
import com.j10d207.tripeer.place.db.repository.ElasticSpotRepository;
import com.j10d207.tripeer.place.db.repository.TownRepository;
import com.j10d207.tripeer.plan.db.TimeEnum;
import com.j10d207.tripeer.plan.dto.req.*;
import com.j10d207.tripeer.plan.dto.res.*;
import com.j10d207.tripeer.plan.event.CompletePlanEvent;
import com.j10d207.tripeer.plan.event.CoworkerDto;
import com.j10d207.tripeer.plan.event.InviteCoworkerEvent;
import com.j10d207.tripeer.tmap.db.TmapErrorCode;
import com.j10d207.tripeer.tmap.db.dto.PublicRootDTO;
import com.j10d207.tripeer.tmap.db.dto.RootInfoDTO;
import com.j10d207.tripeer.user.dto.res.UserDTO;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.kakao.service.KakaoService;
import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import com.j10d207.tripeer.place.db.repository.SpotInfoRepository;
import com.j10d207.tripeer.plan.db.dto.*;
import com.j10d207.tripeer.plan.db.entity.*;
import com.j10d207.tripeer.plan.db.repository.*;
import com.j10d207.tripeer.tmap.db.dto.CoordinateDTO;
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

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
    private final ElasticSpotRepository elasticSpotRepository;

    private final TMapService tMapService;

    private final KakaoService kakaoService;

    private final ApplicationEventPublisher publisher;
    private final WebClient webClient;
    private final CityRepository cityRepository;
    private final TownRepository townRepository;

    private final int SEARCH_PER_PAGE = 10;
    private final int OPTION_KAKAO_CAR = 0;
    private final int OPTION_TMAP_PUBLIC = 1;
    private final int OPTION_TMAP_FERRY_AIR = 2;

    //플랜 생성
    /*
    플랜 생성 요청이 들어오면
    1. 플랜 엔티티 생성
    2. 생성한 유저를 플랜의 coworker(참가자)에 추가
    3. 플랜의 여행지 목록 생성
    4. 요청된 일자만큼 플랜 데이 생성
    5. 이메일 전송
    6. node 서버에 y-doc 탬플릿 생성 요청 보내기
     */
    @Override
    public PlanDetailMainDTO.CreateResultInfo createPlan(PlanCreateInfoReq planCreateInfoReq, long userId) {
        PlanDetailMainDTO.CreateResultInfo createResultInfo = PlanDetailMainDTO.CreateResultInfo.fromPlanCreateInfoReq(planCreateInfoReq);
        //플랜 생성 + 플랜번호 등록
        PlanEntity plan = planRepository.save(PlanEntity.fromDto(createResultInfo));
        createResultInfo.setPlanId(plan.getPlanId());
        //생성된 플랜을 가지기
        UserEntity user = userRepository.findByUserId(userId);
        coworkerRepository.save(CoworkerEntity.createNewEntity(user, plan));

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
        // 여행 시작 및 다이어리 완성 알림 (플랜 생성시 나밖에 없으므로 나에게만 알림 생성)
        publisher.publishEvent(CompletePlanEvent.builder()
            .startAt(planCreateInfoReq.getStartDay())
            .endAt(planCreateInfoReq.getEndDay())
            .planTitle(planCreateInfoReq.getTitle())
            .coworkers(List.of(new CoworkerDto(user.getUserId(), user.getNickname())))
            .build()
        );
        // city-town 정보 누락 이슈에 대하여
        // 1. 플랜 생성시에는 플랜타운이 가지고 있는 city나 town이 가지고 있는 데이터 중 id값 빼고는 null인 임시 객체라서 데이터를 들고올 수 없었다
        // planTownEntityList.stream().map(TownDTO::fromPlanTownEntity).toList() 이거 하면 데이터가 없음
        // 2. 플랜타운 세이브 이후 db에서 플랜타운을 들고와서 city town 을 들고 와도 null 이었다.
        // List<PlanTownEntity> pt = planTownRepository.findByPlan_PlanId(plan.getPlanId());
        // System.out.println(pt.getFirst().getCityOnly().toString());
        // 출력 : CityEntity(cityId=3, description=null, cityImg=null, cityName=null, latitude=0.0, longitude=0.0)
        // 3. 그래서 결국 각 city_id town_id를 가지고 db에서 직접 조회해야 이름, 이미지, 설명, 위도, 경도를 들고 올 수 밖에 없었다.
        List<TownDTO> townDTOList = planTownEntityList.stream()
            .map(planTown -> {
                if (planTown.getTown() == null) {
                    CityEntity city = cityRepository.findByCityId(planTown.getCityOnly().getCityId())
                        .orElseThrow(() -> new CustomException(ErrorCode.CITY_NOT_FOUND));
                    return TownDTO.from(city);
                } else {
                    TownEntity town = townRepository
                        .findByTownPK_TownIdAndTownPK_City_CityId(
                            planTown.getTown().getTownPK().getTownId(),
                            planTown.getTown().getTownPK().getCity().getCityId())
                        .orElseThrow(() -> new CustomException(ErrorCode.TOWN_NOT_FOUND));
                    return TownDTO.from(town);
                }
            }).toList();
        PlanNodeTempleDTO planNodeTempleDTO = new PlanNodeTempleDTO(createResultInfo,
                                                                    UserDTO.Search.fromUserEntity(user),
                                                                    townDTOList);
        webClient.post()
            .uri("/node/plan")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(planNodeTempleDTO)
            .retrieve()
            .bodyToMono(Void.class)  // 응답 본문이 없을 경우
            .doOnSuccess(result -> log.info("Successfully sent request to node server"))
            .doOnError(error -> log.error("Failed to send request to node server", error))
            .subscribe();  // 비동기 처리
        return createResultInfo;
    }

    //플랜 이름 변경
    @Override
    public void changeTitle(TitleChangeReq titleChangeReq, long userId) {
        PlanEntity plan = planRepository.findByPlanId(titleChangeReq.getPlanId());

        if(coworkerRepository.existsByPlan_PlanIdAndUser_UserIdAndRole(titleChangeReq.getPlanId(), userId, "member")) {
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
                            coworkerRepository.findByPlan_PlanIdAndRole(plan.getPlanId(), "member") // 플랜의 멤버 리스트 넣기
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
        if(!coworkerRepository.existsByPlan_PlanIdAndUser_UserIdAndRole(planId, userId, "member")) {
            throw new CustomException(ErrorCode.NOT_HAS_COWORKER);
        }
        List<PlanTownEntity> planTown = planTownRepository.findByPlan_PlanId(plan.getPlanId());
        List<CoworkerEntity> coworkerEntityList = coworkerRepository.findByPlan_PlanIdAndRole(plan.getPlanId(), "member");

        //선택한 도시 목록 구성
        List<TownDTO> townDTOList = planTown.stream().map(TownDTO::fromPlanTownEntity).toList();

        return new PlanDetailMainDTO.MainPageInfo(planId, plan.getTitle(), townDTOList, coworkerEntityList.stream().map(UserDTO.Search::fromCoworkerEntity).toList());
    }

    //동행자 초대
    @Override
    public void invitePlan(CoworkerInvitedReq coworkerInvitedReq, long userId) {
        //로그인 사용자가 소유하지 않은 플랜 접근시
        if(!coworkerRepository.existsByPlan_PlanIdAndUser_UserIdAndRole(coworkerInvitedReq.getPlanId(), userId, "member")) {
            throw new CustomException(ErrorCode.NOT_HAS_COWORKER);
        }

        //이미 초대된 상태 or 멤버인 상태 확인
        if(coworkerRepository.existsByPlan_PlanIdAndUser_UserId(coworkerInvitedReq.getPlanId(), coworkerInvitedReq.getUserId())) {
            throw new CustomException(ErrorCode.HAS_COWORKER);
        }

        //너무 많은 플랜을 가진건 아닌지 확인
        if(coworkerRepository.findByUser_UserIdAndPlan_EndDateAfter(coworkerInvitedReq.getUserId(), LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1)).size() > 5) {
            throw new CustomException(ErrorCode.TOO_MANY_PLAN);
        }

        // 초대자 조회
        UserEntity invitor = userRepository.findByUserId(userId);

        CoworkerEntity coworkerEntity = CoworkerEntity.createInviteEntity(coworkerInvitedReq.getUserId(), coworkerInvitedReq.getPlanId(), userId);
        coworkerRepository.save(coworkerEntity);
        // 유저 닉네임을 들고오기 위해 유저 객체가 필요
        UserEntity user = userRepository.findByUserId(coworkerInvitedReq.getUserId());

        // 플랜 초대 알림
        PlanEntity coworkerPlan = planRepository.findByPlanId(coworkerInvitedReq.getPlanId());

        // invitor: 초대 보낸사람
        // invitedCoworker: 초대 받은사람의 정보임
        publisher.publishEvent(InviteCoworkerEvent.builder()
            .planTitle(coworkerPlan.getTitle())
            .invitor(new CoworkerDto(invitor.getUserId(), invitor.getNickname()))
            .invitedCoworker(new CoworkerDto(user.getUserId(), user.getNickname()))
            .planId(coworkerPlan.getPlanId())
            .build()
        );
    }

    //동행자 추가
    @Override
    public void joinPlan(long planId, long userId) {
        PlanEntity planEntity = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PLAN));

        Optional<CoworkerEntity> optionalCoworkerEntity = coworkerRepository.findByPlan_PlanIdAndUser_UserId(planId, userId);
        if (optionalCoworkerEntity.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_PLAN);
        }

        CoworkerEntity coworkerEntity = optionalCoworkerEntity.get();
        coworkerEntity.setRole("member");
        coworkerRepository.save(coworkerEntity);
        // ydoc 에 유저 정보 업데이트를 위한 node 서버에 유저 정보 보내기
        UserEntity user = userRepository.findByUserId(userId);
        NodeInviteDTO nodeInviteDTO = NodeInviteDTO.from(userRepository.findByUserId(userId), planEntity);

        // 초대 수락으로 인한 여행 시작 및 다이어리 완성 알림
        publisher.publishEvent(CompletePlanEvent.builder()
            .startAt(planEntity.getStartDate())
            .endAt(planEntity.getEndDate())
            .planTitle(planEntity.getTitle())
            .coworkers(List.of(new CoworkerDto(user.getUserId(), user.getNickname())))
            .build()
        );

        webClient.post()
            .uri("/node/plan/invite")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(nodeInviteDTO)
            .retrieve()
            .bodyToMono(Void.class)  // 응답 본문이 없을 경우
            .doOnSuccess(result -> log.info("Successfully sent request to node server"))
            .doOnError(error -> log.error("Failed to send request to node server", error))
            .subscribe();  // 비동기 처리

    }

    /*
    수락 대기중인 초대 리스트를 반환
    포함내용 -> 초대를 보낸사람, 플랜 정보 일부(생성정보), 현재 멤버 리스트
     */
    @Override
    public List<PlanMemberDto.Pending> getPendingList(long userId) {
        List<CoworkerEntity> pendingPlanList = coworkerRepository.findByUser_UserIdAndRole(userId, "pending");

        // 수락 대기중인 플랜이 없을 때
        if (pendingPlanList.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_PENDING);
        }

        return pendingPlanList.stream().map(coworkerEntity -> {
            List<UserEntity> userEntityList = coworkerRepository.findUserByPlanIdAndRole(coworkerEntity.getPlan().getPlanId(), "member");
            return PlanMemberDto.Pending.ofCoworkerEntity(coworkerEntity, userEntityList.stream().map(UserDTO.Search::fromUserEntity).toList());
        }).toList();


    }

    //동행자 조회
    @Override
    public List<PlanDetailMainDTO.PlanCoworker> getCoworker(long planId) {
        //요청된 플랜의 동행자 목록 조회
        List<CoworkerEntity> coworkerList = coworkerRepository.findByPlan_PlanIdAndRole(planId, "member");
        //DTO로 변환
        AtomicInteger order = new AtomicInteger();
        return coworkerList.stream().map(test -> PlanDetailMainDTO.PlanCoworker.fromCoworkerEntity(test, order.getAndIncrement())).toList();
    }

    //관광지 검색
    @Override
    public SpotSearchResDTO getSpotSearch(long planId, String keyword, int page, int sortType, long userId, int cityId, int townId) {
        Pageable pageable = PageRequest.of(page-1, SEARCH_PER_PAGE);

        Page<SpotInfoEntity> spotInfoEntityPage = spotInfoRepository.searchSpotsOfOption(planId, keyword, sortType, cityId, townId, pageable);

        // 현재 여행 버킷
        Set<Integer> planBucketSet = planBucketRepository.findAllSpotInfoIdsByUserId(userId);
        // 위시리스트
        Set<Integer> wishSet = wishListRepository.findAllSpotInfoIdsByUserId(userId);

        return new SpotSearchResDTO(spotInfoEntityPage.getContent().stream()
                .map(spotInfoEntity -> SpotSearchResDTO.SearchResult.fromSpotInfoEntity(spotInfoEntity,
                        wishSet.contains(spotInfoEntity.getSpotInfoId()),    // 위시리스트에 있는지
                        planBucketSet.contains(spotInfoEntity.getSpotInfoId()))  // 버킷에 있는지
                ).toList(), spotInfoRepository.searchSpotsOfOption(planId, keyword, sortType, cityId, townId, PageRequest.of(page, SEARCH_PER_PAGE)).isEmpty());
    }

    //관광지 줌레벨 검색 + elasticSearch
    @Override
    public SpotSearchResDTO getSpotsInMap(long planId, String keyword, int page, double minLat, double maxLat,
        double minLon, double maxLon, int sortType, long userId) {
        PageRequest pageRequest = PageRequest.of(page-1, SEARCH_PER_PAGE);
        List<Integer> contentTypeIds = ContentTypeEnum.getContentTypeIdListFromSortType(sortType);
        // 지도 영역 내의 관광지 검색
        Page<ElasticSpotEntity> elasticSpotList;
        if (keyword.isEmpty()) {
            elasticSpotList = elasticSpotRepository.AllSpotsInMap(
                minLat, maxLat, minLon, maxLon, contentTypeIds, pageRequest);
        } else {
            elasticSpotList = elasticSpotRepository.searchSpotsInMap(
                minLat, maxLat, minLon, maxLon, keyword, contentTypeIds, pageRequest);
        }
        List<Integer> idList = elasticSpotList.stream().map(ElasticSpotEntity::getId).toList();
        List<SpotInfoEntity> spots = spotInfoRepository.findAllWithReviewsById(idList);
        // 위시리스트
        Set<Integer> wishSet = wishListRepository.findAllSpotInfoIdsByUserId(userId);

        return new SpotSearchResDTO(spots.stream()
            .map(spot -> SpotSearchResDTO.SearchResult.fromSpotInfoEntity(spot,
                wishSet.contains(spot.getSpotInfoId()),    // 위시리스트에 있는지
                false)  // 버킷에 있는지
            )
            .toList(), elasticSpotList.isLast());
    }


    //플랜 버킷 관광지 추가
    @Override
    public void addPlanSpot(long planId, int spotInfoId, long userId) {
        if(planBucketRepository.existsByPlan_PlanIdAndSpotInfo_SpotInfoId(planId, spotInfoId)) {
            throw new CustomException(ErrorCode.HAS_BUCKET);
        }
        if(!(coworkerRepository.existsByPlan_PlanIdAndUser_UserIdAndRole(planId, userId, "member"))) {
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
            if (coworkerRepository.existsByPlan_PlanIdAndUser_UserIdAndRole(planBucketEntity.getPlan().getPlanId(), userId, "member")) {
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
    public List<SpotSearchResDTO.SearchResult> getWishList(long userId, long planId) {
        List<WishListEntity> wishList = wishListRepository.findByUser_UserId(userId);
        return  wishList.stream()
                .map(wishListEntity ->
                SpotSearchResDTO.SearchResult.fromWishListEntity
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
        List<CoworkerEntity> coworkerList = coworkerRepository.findByPlan_PlanIdAndRole(planId, "member");

        return IntStream.range(0, coworkerList.size())
                .filter(i -> userId == coworkerList.get(i).getUser().getUserId()) // userId가 일치하는 인덱스를 필터링
                .mapToObj(i -> PlanDetailMainDTO.PlanCoworker.fromCoworkerEntity(coworkerList.get(i), i)) // 인덱스와 함께 객체로 변환
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_HAS_COWORKER));
    }

    @Override
    public AtoBRes getShortTime2(PlaceListReq placeListReq) {
        // 장소 갯수 카운트 AtoB 이므로 2개가 아니면 throw
        int infoSize = placeListReq.getPlaceList().size();
        if( infoSize < 2) throw new CustomException(ErrorCode.NOT_ENOUGH_INFO);
        else if(infoSize > 2) throw new CustomException(ErrorCode.TOO_MANY_INFO);

        List<String> resultTime = new ArrayList<>();
        List<PublicRootDTO> resultRootDTO = new ArrayList<>();
        AtoBRes result = new AtoBRes();

        // 1. 자동차
        int carTime = kakaoService.getDirections(placeListReq.getPlaceList().getFirst().getLongitude(),
                placeListReq.getPlaceList().getFirst().getLatitude(),
                placeListReq.getPlaceList().getLast().getLongitude(),
                placeListReq.getPlaceList().getLast().getLatitude());
        if( carTime > 300) {
            resultTime.add("경로를 찾을 수 없습니다.");
        } else {
            resultTime.add(CommonMethod.timeToString(carTime));
        }
        resultRootDTO.add(null);

        List<RootInfoDTO> publicRootList = tMapService.useTMapPublic3(placeListReq);

        // 2. 대중교통
        RootInfoDTO publicRoot = publicRootList.getFirst();
        if (publicRoot.getStatus().getCode() < 11) {
            resultTime.add(publicRoot.timeToString());
            resultRootDTO.add(publicRoot.getPublicRoot());
        } else {
            resultTime.add(publicRoot.getStatus().getMessage());
            resultRootDTO.add(null);
        }

        // 3. 항공
        RootInfoDTO AirRoot = publicRootList.get(1);
        if (AirRoot.getStatus().getCode() < 11) {
            resultTime.add(AirRoot.timeToString());
            resultRootDTO.add(AirRoot.getPublicRoot());
        } else {
            resultTime.add(AirRoot.getStatus().getMessage());
            resultRootDTO.add(null);
        }

        // 4. 해운
        RootInfoDTO FerryRoot = publicRootList.get(2);
        if (FerryRoot.getStatus().getCode() < 11) {
            resultTime.add(FerryRoot.timeToString());
            resultRootDTO.add(FerryRoot.getPublicRoot());
        } else {
            resultTime.add(FerryRoot.getStatus().getMessage());
            resultRootDTO.add(null);
        }


        result.setTime(resultTime);
        result.setRootList(resultRootDTO);
        return  result;
    }

    //플랜 최단거리 조정
    @Override
    public OptimizingRes getOptimizingTime2(PlaceListReq placeListReq) throws IOException {
        if(placeListReq.getPlaceList().size() < 3) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_INFO);
        }
        // 전달 받은 정보를 기반으로 좌표 리스트 생성
        List<CoordinateDTO> coordinateDTOList = placeListReq.getPlaceList().stream().map(CoordinateDTO::PlaceToCoordinate2).toList();

        FindRoot root = null;
        if ( placeListReq.getOption() == OPTION_KAKAO_CAR ) {
            root = kakaoService.getOptimizingTime(coordinateDTOList);
        }
        else if ( placeListReq.getOption() == OPTION_TMAP_PUBLIC || placeListReq.getOption() == OPTION_TMAP_FERRY_AIR ) {
            root = tMapService.getOptimizingTime(coordinateDTOList, placeListReq.getOption());
        }
        if (root != null) {
            return refactorResult2(root, placeListReq);
        }
        // root 가 null 경우 -1 set 지웠음 (모순이긴 해서)
        return null;
    }

    private OptimizingRes refactorResult2 (FindRoot root, PlaceListReq placeListReq) {
        List<OptimizingRes.Place> newPlaceList = new ArrayList<>();
        List<AtoBRes> atoBResList = new ArrayList<>();

        List<Integer> resultNumbers = root.getResultNumbers();

        for(int i = 0; i < resultNumbers.size(); i++) {
            OptimizingRes.Place newPlace = OptimizingRes.Place.fromReq(placeListReq.getPlaceList().get(resultNumbers.get(i)));
            newPlaceList.add(newPlace);
        }

        for (int i = 1; i< resultNumbers.size(); i++) {
            RootInfoDTO selectInfo = root.getTimeTable()[resultNumbers.get(i-1)][resultNumbers.get(i)];
            if (selectInfo.getTime() == TimeEnum.ERROR_TIME.getTime()) throw new CustomException(ErrorCode.NOT_FOUND_ROOT);

            PlaceListReq tmpPlaceList = PlaceListReq.builder().
                    placeList(List.of(placeListReq.getPlaceList().get(resultNumbers.get(i-1)),
                                        placeListReq.getPlaceList().get(resultNumbers.get(i))))
                    .build();
            AtoBRes newAtoB = getShortTime2(tmpPlaceList);

            newAtoB.setOption(selectInfo.getStatus().getCode());
            atoBResList.add(newAtoB);

        }


        return OptimizingRes.builder()
                .placeList(newPlaceList)
                .optimizing(atoBResList)
                .build();
    }

    private RootRes MakeRootInfo(RootRes rootRes, JsonElement rootInfo) {
        if(rootInfo == null) {
            List<PublicRootDTO> rootList = new ArrayList<>();
            if(rootRes.getPublicRootList() != null) {
                rootList = rootRes.getPublicRootList();
            }
            rootList.add(null);
            rootRes.setPublicRootList(rootList);
            return rootRes;
        }
        JsonObject infoObject = rootInfo.getAsJsonObject();

        PublicRootDTO publicRoot = PublicRootDTO.fromJson(infoObject);


        List<PublicRootDTO> rootList = new ArrayList<>();
        if(rootRes.getPublicRootList() != null) {
            rootList = rootRes.getPublicRootList();
        }
        rootList.add(publicRoot);
        rootRes.setPublicRootList(rootList);

        return rootRes;
    }

}
