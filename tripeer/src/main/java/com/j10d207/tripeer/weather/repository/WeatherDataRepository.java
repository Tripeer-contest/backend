package com.j10d207.tripeer.weather.repository;

import com.j10d207.tripeer.weather.db.entity.WeatherDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherDataRepository extends JpaRepository<WeatherDataEntity, Integer> {
}
