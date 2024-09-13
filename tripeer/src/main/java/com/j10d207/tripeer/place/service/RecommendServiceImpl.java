package com.j10d207.tripeer.place.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.j10d207.tripeer.place.dto.res.RecommendDTO;
import com.j10d207.tripeer.place.dto.res.RecommendSearchDTO;
import com.j10d207.tripeer.place.dto.res.SpotDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import com.j10d207.tripeer.place.db.repository.SpotInfoRepository;
import com.j10d207.tripeer.place.dto.req.RecommendReq;
import com.j10d207.tripeer.plan.db.repository.PlanBucketRepository;
import com.j10d207.tripeer.plan.dto.res.SpotSearchResDTO;
import com.j10d207.tripeer.user.db.repository.WishListRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {
	private final WebClient webClient;
	private final SpotInfoRepository spotInfoRepository;
	private final WishListRepository wishListRepository;
	private final PlanBucketRepository planBucketRepository;

	public List<RecommendDTO> getHomeRecommends(int contentTypeId, int cityId, int townId, long userId) {
		Flux<RecommendReq> responseFlux = webClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/recommend/spring/home")
				.queryParam("contentType", contentTypeId)
				.queryParam("cityId", cityId)
				.queryParam("townId", townId)
				.queryParam("userId", userId)
				.build())
			.retrieve()
			.bodyToFlux(RecommendReq.class);

		List<RecommendReq> recommendReqList = responseFlux.collectList().block();

		Set<Integer> wishList = wishListRepository.findAllSpotInfoIdsByUserId(userId);

		// 각 스팟을 한번에 하나씩 들고 오는것이 아닌 한번에 들고 오기위해서 id 리스트를 만들고
		List<Integer> allSpotInfoIds = recommendReqList.stream()
			.flatMap(recommendReq -> recommendReq.getIdList().stream())
			.distinct()
			.toList();

		//각 스팟의 Map{ id : 객체 } 을 만들어서 아래에서 dto로 만든다.
		Map<Integer, SpotInfoEntity> spotInfoMap = spotInfoRepository.findAllById(allSpotInfoIds).stream()
			.collect(Collectors.toMap(SpotInfoEntity::getSpotInfoId, Function.identity()));

		return recommendReqList.stream()
			.map(recommendReq -> RecommendDTO.builder()
				.comment(recommendReq.getComment())
				.keyword(recommendReq.getKeyword())
				.spotInfoDtos(recommendReq.getIdList().stream()
					.map(el -> SpotDTO.SpotInfoDTO.convertToDto(
						spotInfoMap.get(el),
						wishList.contains(el))
					)
					.toList())
				.build())
			.toList();
	}

	public RecommendDTO getKeywordRecommends(String keyword, int cityId, int townId, long userId){
		Mono<RecommendReq> recommendVOMono = webClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/recommend/spring/keyword")
				.queryParam("keyword", keyword)
				.queryParam("cityId", cityId)
				.queryParam("townId", townId)
				.build())
			.retrieve()
			.bodyToMono(RecommendReq.class);

		Set<Integer> wishList = wishListRepository.findAllSpotInfoIdsByUserId(userId);

		RecommendReq recommendReq = recommendVOMono.block();
		return RecommendDTO.builder()
			.comment(recommendReq.getComment())
			.keyword(recommendReq.getKeyword())
			.spotInfoDtos(recommendReq.getIdList().stream()
				.map(el -> SpotDTO.SpotInfoDTO.convertToDto(spotInfoRepository.findBySpotInfoId(el), wishList.contains(el)))
				.toList())
			.build();
	};

	public List<RecommendSearchDTO> getPlanRecommends(int planId, int cityId, int townId, long userId) {
		Flux<RecommendReq> responseFlux = webClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/recommend/spring/plan")
				.queryParam("planId", planId)
				.queryParam("cityId", cityId)
				.queryParam("townId", townId)
				.queryParam("userId", userId)
				.build())
			.retrieve()
			.bodyToFlux(RecommendReq.class);

		List<RecommendReq> recommendReqList = responseFlux.collectList().block();

		Set<Integer> wishList = wishListRepository.findAllSpotInfoIdsByUserId(userId);

		Set<Integer> bucket = planBucketRepository.findAllSpotInfoIdsByUserId(userId);

		// 각 스팟을 한번에 하나씩 들고 오는것이 아닌 한번에 들고 오기위해서 id 리스트를 만들고
		List<Integer> allSpotInfoIds = recommendReqList.stream()
			.flatMap(recommendReq -> recommendReq.getIdList().stream())
			.distinct()
			.toList();

		//각 스팟의 Map{ id : 객체 } 을 만들어서 아래에서 dto로 만든다.
		Map<Integer, SpotInfoEntity> spotInfoMap = spotInfoRepository.findAllById(allSpotInfoIds).stream()
			.collect(Collectors.toMap(SpotInfoEntity::getSpotInfoId, Function.identity()));

		return recommendReqList.stream()
			.map(recommendReq -> RecommendSearchDTO.builder()
				.comment(recommendReq.getComment())
				.keyword(recommendReq.getKeyword())
				.spotInfoDtos(recommendReq.getIdList().stream()
					.map(el -> SpotSearchResDTO.fromSpotInfoEntity(
						spotInfoMap.get(el),
						wishList.contains(el),
						bucket.contains(el))
					)
					.toList())
				.build())
			.toList();
	}
}
