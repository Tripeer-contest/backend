package com.j10d207.tripeer.history.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.j10d207.tripeer.exception.ErrorResponseEntity;
import com.j10d207.tripeer.history.dto.req.CostReq;
import com.j10d207.tripeer.history.dto.req.GalleryIdListReq;
import com.j10d207.tripeer.history.dto.req.PlanSaveReq;
import com.j10d207.tripeer.history.dto.res.CostRes;
import com.j10d207.tripeer.history.dto.res.GalleryRes;
import com.j10d207.tripeer.history.dto.res.HistoryDetailRes;
import com.j10d207.tripeer.history.dto.res.PlanInfoRes;
import com.j10d207.tripeer.response.Response;
import com.j10d207.tripeer.user.dto.res.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "지난 여행 관련 Controller", description = "플랜 저장 이후부터의 지난 여행을 관리")
public interface HistoryControllerDocs {

	@Operation(summary = "유저의 지난 여행 리스트 조회 요청", description = "jwt 로 해당 유저의 지난여행 리스트를 조회할 수 있다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "여행 리스트 조회 성공"),
		@ApiResponse(responseCode = "204", description = "여행 리스트 조회는 성공했으나 비었을 경우",
			content = @Content(schema = @Schema(implementation = Response.class)))
	})
	Response<List<PlanInfoRes>> getPlanList(@AuthenticationPrincipal CustomOAuth2User user);

	@Operation(summary = "지난 여행 디테일 조회 요청", description = "planId 로 해당 지난여행의 디테일을 조회할 수 있다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "여행 디테일 조회 성공"),
		@ApiResponse(responseCode = "403", description = "해당 여행의 유저가 아닙니다.",
			content = @Content(schema = @Schema(implementation = ErrorResponseEntity.class)))
	})
	Response<HistoryDetailRes> getPlanDetail(@PathVariable("planId") long planId);

	@Operation(summary = "갤러리에 이미지, 동영상 저장 요청", description = "MultipartForm 을 통해서 이미지를 저장할 수 있다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "이미지 저장 성공"),
		@ApiResponse(responseCode = "400", description = "허용되지 않는 확장자를 넣은 경우",
			content = @Content(schema = @Schema(implementation = ErrorResponseEntity.class))),
		@ApiResponse(responseCode = "403", description = "해당 여행의 유저가 아닙니다.",
			content = @Content(schema = @Schema(implementation = ErrorResponseEntity.class))),
		@ApiResponse(responseCode = "503", description = "S3 Upload 에 실패했을 때",
			content = @Content(schema = @Schema(implementation = ErrorResponseEntity.class)))
	})
	public Response<List<GalleryRes>> uploadsImageAndMovie(
		@AuthenticationPrincipal CustomOAuth2User user,
		@PathVariable("planDayId") long planDayId,
		@RequestPart(value = "images") List<MultipartFile> multipartFiles);

	@Operation(summary = "갤러리 리스트 조회 요청", description = "planDayId 로 해당 여행의 이미지 리스트를 조회할 수 있다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "이미지 리스트 조회 성공"),
		@ApiResponse(responseCode = "403", description = "해당 여행의 유저가 아닙니다.",
			content = @Content(schema = @Schema(implementation = ErrorResponseEntity.class))),
	})
	Response<List<GalleryRes>> getGalleryList(@PathVariable("planDayId") long planDayId);

	@Operation(summary = "비용 등록 요청", description = "관광지 마다의 비용을 등록할 수 있다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "비용 등록 성공"),
		@ApiResponse(responseCode = "403", description = "해당 여행의 유저가 아닙니다.",
			content = @Content(schema = @Schema(implementation = ErrorResponseEntity.class))),
		@ApiResponse(responseCode = "400", description = "비용은 0보다 작을 수 없습니다.",
			content = @Content(schema = @Schema(implementation = ErrorResponseEntity.class))),
	})
	Response<CostRes> postCost(@RequestBody CostReq costReq);

	@Operation(summary = "갤러리에서 이미지 삭제 요청", description = "galleryIdList 로 해당 이미지들을 삭제 할 수 있다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "이미지 삭제 성공"),
		@ApiResponse(responseCode = "403", description = "해당 여행의 유저가 아닙니다.",
			content = @Content(schema = @Schema(implementation = ErrorResponseEntity.class))),
		@ApiResponse(responseCode = "503", description = "S3 Upload 에 실패했을 때",
			content = @Content(schema = @Schema(implementation = ErrorResponseEntity.class)))
	})
	Response<String> deleteResources(@RequestBody GalleryIdListReq galleryIdList);

	@Operation(summary = "플랜 저장 요청", description = "작성완료한 여행일정을 저장할 수 있다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "플랜 저장 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 또는 파라미터 입니다.",
			content = @Content(schema = @Schema(implementation = ErrorResponseEntity.class))),
		@ApiResponse(responseCode = "403", description = "해당 여행의 유저가 아닙니다.",
			content = @Content(schema = @Schema(implementation = ErrorResponseEntity.class))),
	})
	Response<String> savePlanDetail(@RequestBody PlanSaveReq planSaveReq);

	@Operation(summary = "플랜 저장 취소", description = "저장한 플랜을 수정을 위해 저장을 취소할 수 있다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "플랜 저장 취소 성공"),
		@ApiResponse(responseCode = "403", description = "해당 여행의 유저가 아닙니다.",
			content = @Content(schema = @Schema(implementation = ErrorResponseEntity.class))),
	})
	Response<String> revokePlanDetail(@PathVariable("planId") long planId);
}
