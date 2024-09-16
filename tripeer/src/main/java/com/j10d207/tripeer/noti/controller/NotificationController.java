package com.j10d207.tripeer.noti.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.j10d207.tripeer.noti.service.NotificationService;
import com.j10d207.tripeer.response.Response;
import com.j10d207.tripeer.user.dto.res.CustomOAuth2User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/noti")

/**
 * 알림 처리 API Controller
 */

public class NotificationController {

	private final NotificationService service;

	/**
	 * @author: 김회창
	 *
	 * <p>
	 *     해당 유저의 Firebase token을 DB에 추가한다.
	 * </p>
	 *
	 * @param: 	user 객체
	 * @status: 성공시 204 NO_CONTENT
	 * @body: 	null
	 *
	 */

	@PostMapping
	public Response<Void> addFirebaseToken(
		@AuthenticationPrincipal CustomOAuth2User user,
		@RequestParam String token
	) {
		service.addFirebaseToken(user.getUserId(), token);
		return Response.of(
			ResponseHeader.FIREBASE_ADDED.getStatus(),
			ResponseHeader.FIREBASE_ADDED.getMessage(),
			null
		);
	}


	@Getter
	@RequiredArgsConstructor
	private enum ResponseHeader {

		FIREBASE_ADDED("토큰 추가에 성공했습니다.", HttpStatus.NO_CONTENT)	;

		private final String message;

		private final HttpStatus status;

	}
}
