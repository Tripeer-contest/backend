package com.j10d207.tripeer.history.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.j10d207.tripeer.history.dto.req.CostReq;
import com.j10d207.tripeer.history.dto.req.GalleryIdListReq;
import com.j10d207.tripeer.history.dto.req.PlanSaveReq;
import com.j10d207.tripeer.history.dto.res.CostRes;
import com.j10d207.tripeer.history.dto.res.GalleryRes;
import com.j10d207.tripeer.history.dto.res.HistoryDetailRes;
import com.j10d207.tripeer.history.dto.res.PlanInfoRes;
import com.j10d207.tripeer.history.service.GalleryService;
import com.j10d207.tripeer.history.service.HistoryService;
import com.j10d207.tripeer.response.Response;
import com.j10d207.tripeer.user.dto.res.CustomOAuth2User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/history")
public class HistoryController implements HistoryControllerDocs {

	final HistoryService historyService;
	final GalleryService galleryService;

	@GetMapping
	public Response<List<PlanInfoRes>> getPlanList(@AuthenticationPrincipal CustomOAuth2User user) {
		try {
			List<PlanInfoRes> planList = historyService.historyList(user.getUserId());
			return Response.of(HttpStatus.OK, "내 다이어리 리스트 조회", planList);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@PostMapping("/gallery/upload/{planDayId}")
	public Response<List<GalleryRes>> uploadsImageAndMovie(
		@AuthenticationPrincipal CustomOAuth2User user,
		@PathVariable("planDayId") long planDayId,
		@RequestPart(value = "images") List<MultipartFile> multipartFiles) {
		List<GalleryRes> galleryList = galleryService.uploadsImageAndMovie(multipartFiles, user.getUserId(), planDayId);
		return Response.of(HttpStatus.OK, "업로드 성공", galleryList);
	}

	@GetMapping("/gallery/{planDayId}")
	public Response<List<GalleryRes>> getGalleryList(@PathVariable("planDayId") long planDayId) {
		try {
			List<GalleryRes> galleryList = galleryService.getGalleryList(planDayId);
			return Response.of(HttpStatus.OK, "갤러리 조회 성공", galleryList);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@PostMapping("/cost")
	public Response<CostRes> postCost(@RequestBody CostReq costReq) {
		try {
			CostRes costRes = historyService.postCost(costReq);
			return Response.of(HttpStatus.OK, "비용 등록 성공", costRes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@PostMapping("/gallery/delete")
	public Response<String> deleteResources(@RequestBody GalleryIdListReq galleryIdList) {
		try {
			String res = galleryService.deleteGalleryList(galleryIdList.getGalleryIdList());
			return Response.of(HttpStatus.OK, "사진 삭제 성공", res);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// @PostMapping("/save")
	// public Response<String> savePlanDetail(@RequestBody PlanSaveReq planSaveReq) {
	// 	String res = historyService.savePlanDetail(planSaveReq);
	// 	return Response.of(HttpStatus.OK, "다이어리 디테일 생성 성공", res);
	// }

	@GetMapping("/{planId}")
	public Response<HistoryDetailRes> getPlanDetail(
		@AuthenticationPrincipal CustomOAuth2User user,
		@PathVariable("planId") long planId) {
		HistoryDetailRes res = historyService.getHistoryDetail(planId, user.getUserId());
		return Response.of(HttpStatus.OK, "다이어리 디테일 조회 성공", res);
	}

	@DeleteMapping("/{planId}")
	public Response<String> deletePlanDetail(
		@AuthenticationPrincipal CustomOAuth2User user,
		@PathVariable("planId") long planId) {
		String res = historyService.deleteHistoryDetail(planId, user.getUserId());
		return Response.of(HttpStatus.NO_CONTENT, "다이어리 삭제 성공", res);
	}

	@PutMapping("/revoke/{planId}")
	public Response<String> revokePlanDetail(@PathVariable("planId") long planId) {
		try {
			String res = historyService.revokeHistoryDetail(planId);
			return Response.of(HttpStatus.OK, "플랜 복원 성공", res);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
