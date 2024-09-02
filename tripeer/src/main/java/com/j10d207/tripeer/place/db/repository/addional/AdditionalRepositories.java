package com.j10d207.tripeer.place.db.repository.addional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Getter
public class AdditionalRepositories {


    private final AdditionalLeportsRepository additionalLeportsRepository;
    private final AdditionalCultureFacilityRepository additionalCultureFacilityRepository;
    private final AdditionalFestivalRepository additionalFestivalRepository;
    private final AdditionalFoodRepository additionalFoodRepository;
    private final AdditionalLodgingRepository additionalLodgingRepository;
    private final AdditionalShoppingRepository additionalShoppingRepository;
    private final AdditionalTourCourseRepository additionalTourCourseRepository;
    private final AdditionalTourismRepository additionalTourismRepository;

}
