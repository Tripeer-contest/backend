package com.j10d207.tripeer.place.service;

import com.j10d207.tripeer.place.db.dto.CityAndTownDTO;

import java.util.List;

public interface TownService {

    public List<CityAndTownDTO.TownListDTO> searchTown(String cityId, String townName);
    public CityAndTownDTO.TownListDTO townDetail(String townName);
    public CityAndTownDTO getAllCityAndTown();
}
