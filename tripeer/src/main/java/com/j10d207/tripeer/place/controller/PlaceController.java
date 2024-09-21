package com.j10d207.tripeer.place.controller;

import com.j10d207.tripeer.kakao.db.entity.BlogInfoResponse;
import com.j10d207.tripeer.place.dto.req.ReviewReq;
import com.j10d207.tripeer.place.dto.req.SpotAddReq;
import com.j10d207.tripeer.place.dto.res.RecommendDTO;
import com.j10d207.tripeer.place.dto.res.RecommendSearchDTO;
import com.j10d207.tripeer.place.dto.res.ReviewDto;
import com.j10d207.tripeer.place.dto.res.SpotDTO;
import com.j10d207.tripeer.place.dto.res.SpotDetailPageDto;
import com.j10d207.tripeer.place.service.RecommendService;
import com.j10d207.tripeer.place.service.ReviewService;
import com.j10d207.tripeer.place.service.SpotService;
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

    private final SpotService spotService;
    private final ReviewService reviewService;
    private final RecommendService recommendService;

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
    @PostMapping("/spot/create")
    public Response<SpotDTO.SpotAddResDTO> createNewSpot(@RequestBody @Valid SpotAddReq spotAddReq, @AuthenticationPrincipal CustomOAuth2User user) {

        return Response.of(HttpStatus.OK, "새로운 스팟 생성", spotService.createNewSpot(spotAddReq, user.getUserId()));
    }

    /*
    리뷰 작성하기 + 별점
     */
    @PostMapping("review/write")
    public Response<?> createReview(@AuthenticationPrincipal CustomOAuth2User user, @RequestBody @Valid ReviewReq reviewReq) {
        reviewService.saveReview(user.getUserId(), reviewReq);
        return Response.of(HttpStatus.OK, "리뷰 작성 완료", null);
    }

    // 홈화면 추천 api
    @GetMapping("/recommend/home")
    public Response<List<RecommendDTO>> getHomeRecommend(@RequestParam("contentTypeId") int contentTypeId,
                                                         @RequestParam("cityId") Integer cityId,
                                                         @RequestParam("townId") Integer townId,
                                                         @AuthenticationPrincipal CustomOAuth2User user) {
        return Response.of(HttpStatus.OK, "홈 추천 조회", recommendService.getHomeRecommends(contentTypeId, cityId, townId, user.getUserId()));
    }

    // 추천 더보기를 위한 api
    @GetMapping("/recommend/keyword")
    public Response<RecommendDTO> getKeywordRecommends(@RequestParam("keyword") String keyword,
                                                       @RequestParam("cityId") Integer cityId,
                                                       @RequestParam("townId") Integer townId,
                                                       @AuthenticationPrincipal CustomOAuth2User user) {
        return Response.of(HttpStatus.OK, "키워드 추천 조회", recommendService.getKeywordRecommends(keyword, cityId, townId, user.getUserId()));
    }

    // 플랜에서 사용할 추천 api
    @GetMapping("/recommend/plan")
    public Response<List<RecommendSearchDTO>> getPlanRecommends(@RequestParam("cityId") Integer cityId,
                                                                @RequestParam("townId") Integer townId,
                                                                @RequestParam("planId") Integer planId,
                                                                @AuthenticationPrincipal CustomOAuth2User user) {
        return Response.of(HttpStatus.OK, "홈 추천 조회", recommendService.getPlanRecommends(planId, cityId, townId, user.getUserId()));
    }

}
