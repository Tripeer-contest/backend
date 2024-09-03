package com.j10d207.tripeer.place.db.repository.additional;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.j10d207.tripeer.place.db.entity.additional.AdditionalBaseEntity;
import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdditionalBaseRepository {

	private final Map<Integer, AdditionalRepository<? extends AdditionalBaseEntity>> repositoryMap = new HashMap<>();

	private final AdditionalCultureFacilityRepository cultureFacilityRepository;
	private final AdditionalTourismRepository tourismRepository;
	private final AdditionalLeportsRepository leportsRepository;
	private final AdditionalFestivalRepository festivalRepository;
	private final AdditionalLodgingRepository lodgingRepository;
	private final AdditionalFoodRepository foodRepository;
	private final AdditionalTourCourseRepository tourCourseRepository;
	private final AdditionalShoppingRepository shoppingRepository;

	@PostConstruct
	public void initializeRepositoryMap() {
		repositoryMap.put(14, cultureFacilityRepository);
		repositoryMap.put(12, tourismRepository);
		repositoryMap.put(28, leportsRepository);
		repositoryMap.put(15, festivalRepository);
		repositoryMap.put(32, lodgingRepository);
		repositoryMap.put(39, foodRepository);
		repositoryMap.put(25, tourCourseRepository);
		repositoryMap.put(38, shoppingRepository);
	}

	public AdditionalBaseEntity findBySpotInfo(SpotInfoEntity spotInfo) {
		AdditionalRepository<? extends AdditionalBaseEntity> repository = repositoryMap.get(spotInfo.getContentTypeId());
		if (repository != null) {
			return repository.findBySpotInfoId(spotInfo.getSpotInfoId())
				.orElse(null);
		}
		return null;
	}
}
