package com.j10d207.tripeer.place.service;

import com.j10d207.tripeer.place.db.dto.CityAndTownDTO;

import java.util.List;

public interface CityService {

    public List<CityAndTownDTO.CityListDTO> searchCity(String cityName);
}
