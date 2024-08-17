package com.j10d207.tripeer.history.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.history.dto.req.CostReq;
import com.j10d207.tripeer.history.dto.req.PlanSaveReq;
import com.j10d207.tripeer.history.dto.res.CostRes;
import com.j10d207.tripeer.history.dto.res.HistoryDetailRes;
import com.j10d207.tripeer.history.dto.res.PlanInfoRes;
import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import com.j10d207.tripeer.place.db.repository.SpotInfoRepository;
import com.j10d207.tripeer.plan.db.entity.PlanDayEntity;
import com.j10d207.tripeer.plan.db.entity.PlanDetailEntity;
import com.j10d207.tripeer.plan.db.entity.PlanEntity;
import com.j10d207.tripeer.plan.db.repository.PlanDayRepository;
import com.j10d207.tripeer.plan.db.repository.PlanDetailRepository;
import com.j10d207.tripeer.plan.db.repository.PlanRepository;
import com.j10d207.tripeer.tmap.db.entity.PublicRootEntity;
import com.j10d207.tripeer.tmap.db.repository.PublicRootRepository;
import com.j10d207.tripeer.user.db.entity.CoworkerEntity;
import com.j10d207.tripeer.user.db.repository.CoworkerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoryServiceImpl implements HistoryService {
	private final CoworkerRepository coworkerRepository;
	private final PlanRepository planRepository;
	private final PlanDetailRepository planDetailRepository;
	private final PlanDayRepository planDayRepository;
	private final SpotInfoRepository spotInfoRepository;
	private final PublicRootRepository publicRootRepository;

	ObjectMapper objectMapper = new ObjectMapper();

	public List<PlanInfoRes> historyList(long userId) {
		List<CoworkerEntity> coworkerList = Optional.ofNullable(coworkerRepository.findByUser_UserId(userId))
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_HAS_COWORKER));
		return coworkerList.stream()
			.map(CoworkerEntity::getPlan)
			.filter(plan -> plan.getVehicle().equals("history"))
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

	// 프론트 데이터 무결성 문제를 해결후 코드 수정하기
	public String savePlanDetail(@RequestBody PlanSaveReq planSaveReq) {
		List<List<Map<String, String>>> totalYList = planSaveReq.getTotalYList();
		List<List<Object>> timeYList = planSaveReq.getTimeYList();
		long planId = planSaveReq.getPlanId();
		List<PlanDayEntity> planDayEntityList = planDayRepository.findAllByPlan_PlanIdOrderByDayAsc(planId);
		PlanEntity planEntity = planRepository.findByPlanId(planId);
		if (planEntity.getVehicle().equals("history")) {
			throw new CustomException(ErrorCode.HISTORY_ALREADY_EXISTS);
		}
		planEntity.setVehicle("history");
		planRepository.save(planEntity);
		List<PlanDetailEntity> revokePlanDetailList = new ArrayList<>();
		for (int day = 1; day < totalYList.size(); day++) {
			for (int step = 0; step < totalYList.get(day).size(); step++) {
				SpotInfoEntity spotInfo = spotInfoRepository.findBySpotInfoId(
					Integer.parseInt(totalYList.get(day).get(step).get("spotInfoId")));
				String howTo = "자동차";     // 자동차 OR 대중교통을 사용하지 않는 description 에 저장
				int hour = 0;
				int min = 0;

				if (step != totalYList.get(day).size() - 1) {
					List<Object> tmp = objectMapper.convertValue(timeYList.get(day).get(step), List.class);
					String time;
					List<Object> timeList = tmp;
					if (timeList.get(1).equals("2")) {
						planDetailRepository.deleteAll(revokePlanDetailList);
						planEntity.setVehicle("private");
						planRepository.save(planEntity);
						throw new CustomException(ErrorCode.UNSUPPORTED_JSON_TYPE);
					}
					if (timeList.get(1).equals("1")) {
						howTo = "대중교통";
					}
					time = timeList.getFirst().toString();
					;

					String[] hourMin = time.split(" ");
					if (hourMin.length == 1) {
						min = Integer.parseInt(hourMin[0].substring(0, hourMin[0].length() - 1));
					} else {
						hour = Integer.parseInt(hourMin[0].substring(0, hourMin[0].length() - 2));
						min = Integer.parseInt(hourMin[1].substring(0, hourMin[1].length() - 1));
					}
				}
				if (howTo.equals("대중교통")) {
					SpotInfoEntity nextSpotInfo = spotInfoRepository.findBySpotInfoId(
						Integer.parseInt(totalYList.get(day).get(step + 1).get("spotInfoId")));
					Optional<PublicRootEntity> optionalPublicRoot = publicRootRepository.findByStartLatAndStartLonAndEndLatAndEndLon(
						spotInfo.getLongitude(), spotInfo.getLatitude(), nextSpotInfo.getLongitude(),
						nextSpotInfo.getLatitude());
					if (optionalPublicRoot.isEmpty())
						throw new CustomException(ErrorCode.UNSUPPORTED_JSON_TYPE);
					PublicRootEntity publicRootEntity = optionalPublicRoot.get();
					PlanDetailEntity planDetail = PlanDetailEntity.builder()
						.planDay(planDayEntityList.get(day - 1))
						.spotInfo(spotInfo)
						.day(day)
						.step(step + 1)
						.description(howTo)
						.spotTime(LocalTime.of(hour, min))
						.publicRoot(publicRootEntity)
						.cost(0)
						.build();
					planDetailRepository.save(planDetail);
					revokePlanDetailList.add(planDetail);
				} else {
					PlanDetailEntity planDetail = PlanDetailEntity.builder()
						.planDay(planDayEntityList.get(day - 1))
						.spotInfo(spotInfo)
						.day(day)
						.step(step + 1)
						.description(howTo)
						.spotTime(LocalTime.of(hour, min))
						.cost(0)
						.build();
					planDetailRepository.save(planDetail);
					revokePlanDetailList.add(planDetail);
				}
			}
		}
		return "ok";
	}

	public HistoryDetailRes getHistoryDetail(long planId) {
		PlanEntity plan = Optional.ofNullable(planRepository.findByPlanId(planId))
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PLAN));
		return HistoryDetailRes.from(plan);
	}

	public String revokeHistoryDetail(long planId) {
		PlanEntity plan = Optional.ofNullable(planRepository.findByPlanId(planId))
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PLAN));
		List<PlanDayEntity> planDayEntityList = planDayRepository.findAllByPlan_PlanIdOrderByDayAsc(planId);
		planDayEntityList.stream()
			.map(PlanDayEntity::getPlanDetailList)  // PlanDayEntity에서 PlanDetailEntity 리스트를 가져옴
			.forEach(planDetailRepository::deleteAll);  // 가져온 각각의 PlanDetailEntity 리스트를 삭제
		plan.setVehicle("private");
		planRepository.save(plan);
		return "성공";
	}
}
