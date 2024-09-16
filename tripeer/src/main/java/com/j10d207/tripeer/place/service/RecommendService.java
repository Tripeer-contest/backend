package com.j10d207.tripeer.place.service;


import com.j10d207.tripeer.place.dto.res.RecommendDTO;
import com.j10d207.tripeer.place.dto.res.RecommendSearchDTO;

import java.util.List;

public interface RecommendService {
	List<RecommendDTO> getHomeRecommends(int ContentTypeId, int cityId, int townId, long userId);

	RecommendDTO getKeywordRecommends(String Keyword, int cityId, int townId, long userId);

	List<RecommendSearchDTO> getPlanRecommends(int planId, int cityId, int townId, long userId);
}
