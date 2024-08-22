package com.j10d207.tripeer.place.controller;

import com.j10d207.tripeer.place.db.dto.*;
import com.j10d207.tripeer.place.db.vo.SpotAddVO;
import com.j10d207.tripeer.place.service.CityService;
import com.j10d207.tripeer.place.service.SpotService;
import com.j10d207.tripeer.place.service.TownService;
import com.j10d207.tripeer.plan.service.PlanService;
import com.j10d207.tripeer.response.Response;
import com.j10d207.tripeer.user.dto.res.CustomOAuth2User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/place")
@AllArgsConstructor
public class PlaceController {

    private final CityService cityService;
    private final TownService townService;
    private final SpotService spotService;
    private final PlanService planService;

    /*
    * city 검색
    * cityName이 -1일 경우 전체 조회
    * 그 외의 경우 해당 city 조회
    * */
    @GetMapping("/city/{cityName}")
    public Response<List<CityListDto>> searchCity(@PathVariable("cityName") String cityName) {
        return Response.of(HttpStatus.OK, "도시 검색 결과", cityService.searchCity(cityName));
    }


    /*
     * town 검색
     * townName이 -1일 경우 전체 조회
     * 그 외의 경우 해당 town 조회
     * */
    @GetMapping("/town")
    public Response<List<TownListDto>> searchTown(@RequestParam("cityId") String cityId,
                                                  @RequestParam("townName") String townName) {
        return Response.of(HttpStatus.OK, "타운 검색 결과", townService.searchTown(cityId, townName));
    }


    /*
     * town 디테일 조회
     * */
    @GetMapping("/detail/{townName}")
    public Response<TownListDto> townDetail(@PathVariable("townName") String townName) {
        return Response.of(HttpStatus.OK, "타운 디테일 조회", townService.townDetail(townName));
    }


    /*
     * 해당 지역(군, 구)
     * 의 숙소 조회
     * */
    @GetMapping("/stay")
    public Response<SpotListDto> getStayList(@RequestParam("cityId") Integer cityId,
                                             @RequestParam("townId") Integer townId,
                                             @RequestParam("page") Integer page,
                                             @AuthenticationPrincipal CustomOAuth2User user) {
        return Response.of(HttpStatus.OK, "숙소 조회", spotService.getSpotByContentType(page,32, cityId, townId, user.getUserId()));
    }


    /*
     * 해당 지역(군, 구)
     * 의 식당 조회
     * */
    @GetMapping("/restaurant")
    public Response<SpotListDto> getRestaurantList(@RequestParam("cityId") Integer cityId,
                                                   @RequestParam("townId") Integer townId,
                                                   @RequestParam("page") Integer page,
                                                   @AuthenticationPrincipal CustomOAuth2User user) {
        return Response.of(HttpStatus.OK, "식당 조회", spotService.getSpotByContentType(page,39, cityId, townId, user.getUserId()));
    }


    /*
     * 해당 지역(군, 구)
     * 의 명소 조회
     * */
    @GetMapping("/mecca")
    public Response<SpotListDto> getmeccaList(@RequestParam("cityId") Integer cityId,
                                              @RequestParam("townId") Integer townId,
                                              @RequestParam("page") Integer page,
                                              @AuthenticationPrincipal CustomOAuth2User user) {
        List<Integer> contentTypeIds = Arrays.asList(32, 39);
        return Response.of(HttpStatus.OK, "명소 조회", spotService.getSpotByContentType(page, contentTypeIds, cityId, townId, user.getUserId()));
    }


    /*
     * 스팟 디테일 조회
     * */
    @GetMapping("/spot/detail/{spotId}")
    public Response<SpotDetailDto> getSpotDetail(@PathVariable("spotId") Integer spotId) {

        return Response.of(HttpStatus.OK, "스팟 디테일 조회", spotService.getSpotDetail(spotId));
    }


    /*
     * 스팟 생성
     * */
    @PostMapping("/spot/create")
    public Response<SpotAddResDto> createNewSpot(@RequestBody SpotAddVO spotAddVO, @AuthenticationPrincipal CustomOAuth2User user) {

        return Response.of(HttpStatus.OK, "새로운 스팟 생성", spotService.createNewSpot(spotAddVO, user.getUserId()));
    }

    /*
    * 모든 도시, 타운 조회
    * */
    @GetMapping("/all")
    public Response<CityAndTownDto> getAllCityAndTown() {
        return Response.of(HttpStatus.OK, "모든 도시, 타운 조회", townService.getAllCityAndTown());
    }

}
