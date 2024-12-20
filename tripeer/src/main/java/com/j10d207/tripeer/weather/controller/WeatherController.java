package com.j10d207.tripeer.weather.controller;

import com.j10d207.tripeer.response.Response;
import com.j10d207.tripeer.weather.db.dto.WeatherDataDTO;
import com.j10d207.tripeer.weather.service.WeatherServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherServiceImpl weatherService;

    @GetMapping("")
    public Response<WeatherDataDTO> getWeatherData(@RequestParam("cityId") int cityId, @RequestParam("townId") int townId) throws IOException {
        return Response.of(HttpStatus.OK, "날씨 데이터", weatherService.checkIsUpdateOrCreate(cityId, townId));
    }
}
