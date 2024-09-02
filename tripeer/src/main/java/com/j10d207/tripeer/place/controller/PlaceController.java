package com.j10d207.tripeer.place.controller;

import com.j10d207.tripeer.kakao.db.entity.BlogInfoResponse;
import com.j10d207.tripeer.place.dto.req.ReviewReq;
import com.j10d207.tripeer.place.dto.req.SpotAddReq;
import com.j10d207.tripeer.place.dto.res.CityAndTownDTO;
import com.j10d207.tripeer.place.dto.res.ReviewDto;
import com.j10d207.tripeer.place.dto.res.SpotDTO;
import com.j10d207.tripeer.place.dto.res.SpotDetailPageDto;
import com.j10d207.tripeer.place.service.CityService;
import com.j10d207.tripeer.place.service.ReviewService;
import com.j10d207.tripeer.place.service.SpotService;
import com.j10d207.tripeer.place.service.TownService;
import com.j10d207.tripeer.plan.service.PlanService;
import com.j10d207.tripeer.response.Response;
import com.j10d207.tripeer.user.dto.res.CustomOAuth2User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    장소 디테일 페이지 첫 로딩 정보
     */
    @GetMapping("/main/{spotId}")
    public Response<SpotDetailPageDto> getSpotDetailMain(@AuthenticationPrincipal CustomOAuth2User user,
                                                         @PathVariable("spotId") int spotId) {
        return Response.of(HttpStatus.OK, "장소 메인 로딩 성공", spotService.getDetailMainPage(user.getUserId(), spotId));
    }

    @GetMapping("/main/review")
    public Response<List<ReviewDto>> getSpotDetailReview(@RequestParam("spotId") int spotId,
                                                         @RequestParam("page") int page) {
        return Response.of(HttpStatus.OK, "리뷰 페이지 로딩 성공", spotService.getReviewPage(spotId, page));
    }

    @GetMapping("/main/bloginfo")
    public Response<List<BlogInfoResponse.Document>> getBlogInfo(@RequestParam("title") String title,
                                                                 @RequestParam("page") int page) {
        return Response.of(HttpStatus.OK, "블로그 추가정보 조회 성공", spotService.getBlogInfoPage(title, page));
    }

    /*
    * city 검색
    * cityName이 -1일 경우 전체 조회
    * 그 외의 경우 해당 city 조회
    * */
    @GetMapping("/city/{cityName}")
    public Response<List<CityAndTownDTO.CityListDTO>> searchCity(@PathVariable("cityName") String cityName) {
        return Response.of(HttpStatus.OK, "도시 검색 결과", cityService.searchCity(cityName));
    }


    /*
     * town 검색
     * townName이 -1일 경우 전체 조회
     * 그 외의 경우 해당 town 조회
     * */
    @GetMapping("/town")
    public Response<List<CityAndTownDTO.TownListDTO>> searchTown(@RequestParam("cityId") String cityId,
                                                                 @RequestParam("townName") String townName) {
        return Response.of(HttpStatus.OK, "타운 검색 결과", townService.searchTown(cityId, townName));
    }


    /*
     * town 디테일 조회
     * */
    @GetMapping("/detail/{townName}")
    public Response<CityAndTownDTO.TownListDTO> townDetail(@PathVariable("townName") String townName) {
        return Response.of(HttpStatus.OK, "타운 디테일 조회", townService.townDetail(townName));
    }



    @GetMapping("/search")
    public Response<SpotDTO.SpotListDTO> getSearchList(@RequestParam("contentTypeId") int contentTypeId,
                                                       @RequestParam("cityId") Integer cityId,
                                                       @RequestParam("townId") Integer townId,
                                                       @RequestParam("page") Integer page,
                                                       @AuthenticationPrincipal CustomOAuth2User user) {
        return Response.of(HttpStatus.OK, "장소 조회", spotService.getSpotSearch(page, contentTypeId, cityId, townId, user.getUserId()));
    }

    /*
     * 스팟 생성 새 장소 등록 코드, 원본 작성자 퇴사 + 리뉴얼 제작성 하는걸로 자체 결정, 새로 쓸때 참고용으로 주석처리 해둠
     * */
//    @PostMapping("/spot/create")
//    public Response<SpotDTO.SpotAddResDTO> createNewSpot(@RequestBody @Valid SpotAddReq spotAddReq, @AuthenticationPrincipal CustomOAuth2User user) {
//
//        return Response.of(HttpStatus.OK, "새로운 스팟 생성", spotService.createNewSpot(spotAddReq, user.getUserId()));
//    }

    /*
    * 모든 도시, 타운 조회
    * */
    @GetMapping("/all")
    public Response<CityAndTownDTO> getAllCityAndTown() {
        return Response.of(HttpStatus.OK, "모든 도시, 타운 조회", townService.getAllCityAndTown());
    }

    /*
    리뷰 작성하기 + 별점
     */
    @PostMapping("review/write")
    public Response<?> createReview(@AuthenticationPrincipal CustomOAuth2User user, @RequestBody @Valid ReviewReq reviewReq) {
        reviewService.saveReview(user.getUserId(), reviewReq);
        return Response.of(HttpStatus.OK, "리뷰 작성 완료", null);
    }

}
