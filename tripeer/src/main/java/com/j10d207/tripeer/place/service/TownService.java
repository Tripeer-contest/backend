package com.j10d207.tripeer.place.service;

import com.j10d207.tripeer.place.db.dto.CityAndTownDto;
import com.j10d207.tripeer.place.db.dto.TownListDto;

import java.util.List;

public interface TownService {

    public List<TownListDto> searchTown(String cityId, String townName);
    public TownListDto townDetail(String townName);
    public CityAndTownDto getAllCityAndTown();
}