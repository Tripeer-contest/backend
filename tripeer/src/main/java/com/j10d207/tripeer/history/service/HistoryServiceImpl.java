package com.j10d207.tripeer.history.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.http.MediaType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.history.dto.req.CostReq;
import com.j10d207.tripeer.history.dto.req.PlanDetailSaveReq;
import com.j10d207.tripeer.history.dto.req.PlanSaveReq;
import com.j10d207.tripeer.history.dto.res.CostRes;
import com.j10d207.tripeer.history.dto.res.HistoryDetailRes;
import com.j10d207.tripeer.history.dto.res.PlanInfoRes;
import com.j10d207.tripeer.place.db.entity.SpotReviewEntity;
import com.j10d207.tripeer.place.db.repository.SpotReviewRepository;
import com.j10d207.tripeer.plan.db.entity.PlanDayEntity;
import com.j10d207.tripeer.plan.db.entity.PlanDetailEntity;
import com.j10d207.tripeer.plan.db.entity.PlanEntity;
import com.j10d207.tripeer.plan.db.repository.PlanDayRepository;
import com.j10d207.tripeer.plan.db.repository.PlanDetailRepository;
import com.j10d207.tripeer.plan.db.repository.PlanRepository;
import com.j10d207.tripeer.user.db.entity.CoworkerEntity;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import com.j10d207.tripeer.user.db.repository.CoworkerRepository;
import com.j10d207.tripeer.user.db.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoryServiceImpl implements HistoryService {
	private final CoworkerRepository coworkerRepository;
	private final PlanRepository planRepository;
	private final PlanDetailRepository planDetailRepository;
	private final PlanDayRepository planDayRepository;
	private final WebClient webClient;
	private final SpotReviewRepository spotReviewRepository;
	private final UserRepository userRepository;

	ObjectMapper objectMapper = new ObjectMapper();

	public List<PlanInfoRes> historyList(long userId) {
		List<CoworkerEntity> coworkerList = Optional.ofNullable(coworkerRepository.findByUser_UserId(userId))
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_HAS_COWORKER));
		return coworkerList.stream()
			.map(CoworkerEntity::getPlan)
			.filter(PlanEntity::getIsSaved)
			.map(PlanInfoRes::from)
			.toList();
	}

	public CostRes postCost(@RequestBody CostReq costReq) {
		PlanDetailEntity planDetail = Optional.ofNullable(
				planDetailRepository.findByPlanDetailId(costReq.getPlanDetailId()))
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PLAN));
		planDetail.setCost(costReq.getCost());
		planDetailRepository.save(planDetail);
		return CostRes.from(planDetail);
	}

	public HistoryDetailRes getHistoryDetail(long planId, long userId) {
		UserEntity user = userRepository.findById(userId).orElseThrow(() ->new CustomException(ErrorCode.USER_NOT_FOUND));
		List<SpotReviewEntity> spotReviewEntities = spotReviewRepository.findSpotInfoIdsByUser(user);
		Map<Integer, Long> spotReviewMap = spotReviewEntities.stream()
			.collect(Collectors.toMap(
				spotReviewEntity -> spotReviewEntity.getSpotInfo().getSpotInfoId(),
				SpotReviewEntity::getSpotReviewId,
				(existing, replacement) -> replacement  // 키가 중복될 경우 대체
			));
		PlanEntity plan = Optional.ofNullable(planRepository.findByPlanForHistory(planId))
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PLAN));
		return HistoryDetailRes.from(plan, spotReviewMap);
	}

	public String revokeHistoryDetail(long planId) {
		PlanEntity plan = Optional.ofNullable(planRepository.findByPlanId(planId))
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PLAN));
		List<PlanDayEntity> planDayEntityList = planDayRepository.findAllByPlan_PlanIdOrderByDayAsc(planId);
		planDayEntityList.stream()
			.map(PlanDayEntity::getPlanDetailList)  // PlanDayEntity에서 PlanDetailEntity 리스트를 가져옴
			.forEach(planDetailRepository::deleteAll);  // 가져온 각각의 PlanDetailEntity 리스트를 삭제
		plan.setIsSaved(false);
		planRepository.save(plan);
		return "복구 성공";
	}

	public String deleteHistoryDetail(long planId, long userId) {
		CoworkerEntity coworker = coworkerRepository.findByPlan_PlanIdAndUser_UserId(planId, userId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PLAN));
		coworkerRepository.delete(coworker);
		return "삭제 성공";
	}

	@Transactional
	@Scheduled(cron = "0 0 3 * * ?")
	public void planSaveScheduleTask(){
		List<Long> unsavedPlanIdList = planRepository.findAllWithUnsavedAndPastEndDate(LocalDate.now());
		log.info("플랜 저장 시작");
		Flux<PlanSaveReq> responseFlux = webClient.post()
			.uri("/node/plan/save")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(unsavedPlanIdList)
			.retrieve()
			.bodyToFlux(PlanSaveReq.class);
		// 저장할려는 플랜 리스트
		List<PlanSaveReq> planSaveReqList = responseFlux.collectList().block();

		planSaveReqList.forEach(planSaveReq -> {
			PlanEntity planEntity = planRepository.findByPlanId(planSaveReq.getPlanId());
			planEntity.setIsSaved(true);
			planRepository.save(planEntity);
			List<PlanDayEntity> planDayEntityList = PlanDayEntity.from(planEntity);
			if (!planDayEntityList.isEmpty()) {
				planDayRepository.saveAll(planDayEntityList);
				IntStream.range(0, planSaveReq.getPlanDayList().size()).forEach(i -> {
					PlanDayEntity planDay = planDayEntityList.get(i);
					List<PlanDetailSaveReq> planDetailSaveReqs = planSaveReq.getPlanDayList().get(i);
					List<PlanDetailEntity> planDetails = planDetailSaveReqs.stream()
						.map(req -> PlanDetailEntity.from(req, planDay))
						.toList();
					planDetailRepository.saveAll(planDetails);
				});
			}

		});
	}

	// 저장 테스트용 api
	@Transactional
	public String savePlanDetail(PlanSaveReq planSaveReq) {
		List<Long> unsavedPlanIdList = Collections.singletonList(planSaveReq.getPlanId());
		Flux<PlanSaveReq> responseFlux = webClient.post()
			.uri("/node/plan/save")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(unsavedPlanIdList)
			.retrieve()
			.bodyToFlux(PlanSaveReq.class);
		// 저장할려는 플랜 리스트
		List<PlanSaveReq> planSaveReqList = responseFlux.collectList().block();

		planSaveReqList.forEach(SaveReq -> {
			PlanEntity planEntity = planRepository.findByPlanId(SaveReq.getPlanId());
			planEntity.setIsSaved(true);
			planRepository.save(planEntity);
			List<PlanDayEntity> planDayEntityList = PlanDayEntity.from(planEntity);
			planDayRepository.saveAll(planDayEntityList);
			IntStream.range(0, planDayEntityList.size()).forEach(i -> {
				PlanDayEntity planDay = planDayEntityList.get(i);
				List<PlanDetailSaveReq> planDetailSaveReqs = SaveReq.getPlanDayList().get(i);
				List<PlanDetailEntity> planDetails = planDetailSaveReqs.stream()
					.map(req -> PlanDetailEntity.from(req, planDay))
					.toList();
				planDetailRepository.saveAll(planDetails);
			});
		});
		return "성공";
	};

}
