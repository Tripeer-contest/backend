package com.j10d207.tripeer.place.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.place.db.dto.RecommendDTO;
import com.j10d207.tripeer.place.db.dto.SpotInfoDto;
import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import com.j10d207.tripeer.place.db.repository.SpotInfoRepository;
import com.j10d207.tripeer.place.db.vo.RecommendVO;
import com.j10d207.tripeer.user.db.entity.WishListEntity;
import com.j10d207.tripeer.user.db.repository.WishListRepository;
import com.j10d207.tripeer.user.dto.res.UserDTO;

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

	public List<RecommendDTO> getHomeRecommends(int contentTypeId, int cityId, int townId, long userId) {
		Flux<RecommendVO> responseFlux = webClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/recommend/spring/home")
				.queryParam("contentType", contentTypeId)
				.queryParam("cityId", cityId)
				.queryParam("townId", townId)
				.queryParam("userId", userId)
				.build())
			.retrieve()
			.bodyToFlux(RecommendVO.class);

		List<RecommendVO> recommendVOList = responseFlux.collectList().block();

		Set<Integer> wishList = wishListRepository.findByUser_UserId(userId).stream()
			.map(el -> el.getSpotInfo().getSpotInfoId())
			.collect(Collectors.toSet());

		List<Integer> allSpotInfoIds = recommendVOList.stream()
			.flatMap(recommendVO -> recommendVO.getIdList().stream())
			.distinct()
			.toList();

		Map<Integer, SpotInfoEntity> spotInfoMap = spotInfoRepository.findAllById(allSpotInfoIds).stream()
			.collect(Collectors.toMap(SpotInfoEntity::getSpotInfoId, Function.identity()));

		return recommendVOList.stream()
			.map(recommendVO -> RecommendDTO.builder()
				.comment(recommendVO.getComment())
				.keyword(recommendVO.getKeyword())
				.spotInfoDtos(recommendVO.getIdList().stream()
					.map(el -> SpotInfoDto.convertToDto(
						spotInfoMap.get(el),
						wishList.contains(el))
					)
					.toList())
				.build())
			.toList();
	}

	public RecommendDTO getKeywordRecommends(String keyword, int cityId, int townId, long userId){
		Mono<RecommendVO> recommendVOMono = webClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/recommend/spring/keyword")
				.queryParam("keyword", keyword)
				.queryParam("cityId", cityId)
				.queryParam("townId", townId)
				.build())
			.retrieve()
			.bodyToMono(RecommendVO.class);

		Set<Integer> wishList = wishListRepository.findByUser_UserId(userId).stream()
			.map(el -> el.getSpotInfo().getSpotInfoId())
			.collect(Collectors.toSet());
		RecommendVO recommendVO = recommendVOMono.block();
		return RecommendDTO.builder()
			.comment(recommendVO.getComment())
			.keyword(recommendVO.getKeyword())
			.spotInfoDtos(recommendVO.getIdList().stream()
				.map(el -> SpotInfoDto.convertToDto(spotInfoRepository.findBySpotInfoId(el), wishList.contains(el)))
				.toList())
			.build();
	};
}
