package com.j10d207.tripeer.history.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import com.j10d207.tripeer.history.db.dto.CostReqDTO;
import com.j10d207.tripeer.history.db.dto.CostResDTO;
import com.j10d207.tripeer.history.db.dto.GalleryDTO;
import com.j10d207.tripeer.history.db.dto.GalleryIdListDTO;
import com.j10d207.tripeer.history.db.dto.HistoryDetailResDTO;
import com.j10d207.tripeer.history.db.dto.PlanSaveReqDTO;
import com.j10d207.tripeer.history.service.GalleryService;
import com.j10d207.tripeer.history.service.HistoryService;
import com.j10d207.tripeer.plan.db.dto.PlanListResDTO;
import com.j10d207.tripeer.response.Response;

import jakarta.servlet.http.HttpServletRequest;
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
	public Response<List<PlanListResDTO>> getPlanList(HttpServletRequest request) {
		try {
			List<PlanListResDTO> planList = historyService.historyList(request.getHeader("Authorization"));
			return Response.of(HttpStatus.OK, "내 다이어리 리스트 조회", planList);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@PostMapping(value = "/gallery/upload/{planDayId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Response<List<GalleryDTO>> uploadsImageAndMovie(
		HttpServletRequest request,
		@PathVariable("planDayId") long planDayId,
		@RequestPart(value = "images") List<MultipartFile> multipartFiles) {
		List<GalleryDTO> galleryList = galleryService.uploadsImageAndMovie(multipartFiles,
			request.getHeader("Authorization"), planDayId);
		return Response.of(HttpStatus.OK, "업로드 성공", galleryList);
	}

	@GetMapping("/gallery/{planDayId}")
	public Response<List<GalleryDTO>> getGalleryList(@PathVariable("planDayId") long planDayId) {
		try {
			List<GalleryDTO> galleryList = galleryService.getGalleryList(planDayId);
			return Response.of(HttpStatus.OK, "갤러리 조회 성공", galleryList);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@PostMapping("/cost")
	public Response<CostResDTO> postCost(@RequestBody CostReqDTO costReqDTO) {
		try {
			CostResDTO costResDTO = historyService.postCost(costReqDTO);
			return Response.of(HttpStatus.OK, "비용 등록 성공", costResDTO);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@DeleteMapping("/gallery/delete")
	public Response<String> deleteResources(@RequestBody GalleryIdListDTO galleryIdList) {
		try {
			String res = galleryService.deleteGalleryList(galleryIdList.getGalleryIdList());
			return Response.of(HttpStatus.OK, "사진 삭제 성공", res);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@PostMapping("/save")
	public Response<String> savePlanDetail(@RequestBody PlanSaveReqDTO planSaveReqDTO) {
		String res = historyService.savePlanDetail(planSaveReqDTO);
		return Response.of(HttpStatus.OK, "플랜 저장 성공", res);
	}

	@GetMapping("/{planId}")
	public Response<HistoryDetailResDTO> getPlanDetail(@PathVariable("planId") long planId) {
		try {
			HistoryDetailResDTO res = historyService.getHistoryDetail(planId);
			return Response.of(HttpStatus.OK, "다이어리 디테일 조회 성공", res);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
