package com.j10d207.tripeer.place.service;

import java.util.List;

import com.j10d207.tripeer.place.db.dto.RecommendDTO;

public interface RecommendService {
	List<RecommendDTO> getHomeRecommends(int ContentTypeId, int cityId, int townId, long userId);

	RecommendDTO getKeywordRecommends(String Keyword, int cityId, int townId, long userId);
}
