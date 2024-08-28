package com.j10d207.tripeer.place.controller;

import com.j10d207.tripeer.place.db.dto.*;
import com.j10d207.tripeer.place.db.vo.ReviewVO;
import com.j10d207.tripeer.place.db.vo.SpotAddVO;
import com.j10d207.tripeer.place.service.CityService;
import com.j10d207.tripeer.place.service.ReviewService;
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
    private final ReviewService reviewService;

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


    @GetMapping("/search")
    public Response<SpotListDto> getSearchList(@RequestParam("contentTypeId") int contentTypeId,
                                               @RequestParam("cityId") Integer cityId,
                                             @RequestParam("townId") Integer townId,
                                             @RequestParam("page") Integer page,
                                             @AuthenticationPrincipal CustomOAuth2User user) {
        return Response.of(HttpStatus.OK, "장소 조회", spotService.getSpotSearch(page, contentTypeId, cityId, townId, user.getUserId()));
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

    /*
    리뷰 작성하기 + 별점
     */
    @PostMapping("review/write")
    public Response<?> createReview(@AuthenticationPrincipal CustomOAuth2User user, @RequestBody ReviewVO reviewVO) {
        reviewService.saveReview(user.getUserId(), reviewVO);
        return Response.of(HttpStatus.OK, "리뷰 작성 완료", null);
    }

}
