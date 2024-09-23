package com.j10d207.tripeer.noti.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.j10d207.tripeer.noti.service.NotificationEventTestPublisher;
import com.j10d207.tripeer.noti.service.FirebaseTokenService;
import com.j10d207.tripeer.noti.service.NotificationService;
import com.j10d207.tripeer.noti.service.TestNotificationService;
import com.j10d207.tripeer.response.Response;
import com.j10d207.tripeer.user.dto.res.CustomOAuth2User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/noti")
@Slf4j

/**
 * 알림 처리 API Controller
 */

public class NotificationController {

	private final FirebaseTokenService firebaseTokenService;
	private final NotificationService notificationService;
	private final TestNotificationService testService;

	/**
	 * @author: 김회창
	 *
	 * <p>
	 *     해당 유저의 Firebase token을 DB에 추가한다.
	 * </p>
	 *
	 * @param user:	로그인 된 유저 객체
	 * @param token: 저장할 Firebase token
	 * @status: 성공시 204 NO_CONTENT
	 * @body: 	null
	 *
	 */

	@PostMapping
	public Response<Void> addFirebaseToken(
		@AuthenticationPrincipal CustomOAuth2User user,
		@RequestParam String token
	) {
		firebaseTokenService.addFirebaseToken(user.getUserId(), token);
		return Response.of(
			ResponseHeader.FIREBASE_ADDED.getStatus(),
			ResponseHeader.FIREBASE_ADDED.getMessage(),
			null
		);
	}

	/**
	 * @author: 김회창
	 *
	 * <p>
	 *     해당 알림의 상태를 SENT 에서 READ 로 변경 한다.
	 * </p>
	 *
	 * @param id: 상태를 변경할 알림의 고유 번호
	 * @status: 성공시 204 NO_CONTENT
	 * @body: 	null
	 *
	 */

	@PatchMapping("/{notificationId}")
	public Response<Void> readNotification(
		@PathVariable("notificationId") Long id
	) {
		notificationService.updateStateToRead(id);
		return Response.of(
			ResponseHeader.NOTI_READ.getStatus(),
			ResponseHeader.NOTI_READ.getMessage(),
			null
		);
	}

	@GetMapping("/test/tripeer")
	public Response<Void> testTripeerStart(
		@AuthenticationPrincipal CustomOAuth2User user,
		@RequestParam String fcmToken,
		@RequestParam String planTitle
	) {
		log.info("param: token={}, planTitle={}", fcmToken, planTitle);
		testService.testTripeerNoti(user.getUserId(), fcmToken, planTitle);

		return Response.of(
			HttpStatus.OK,
			null,
			null
		);
	}

	@GetMapping("/test/diary")
	public Response<Void> testDiarySave(
		@AuthenticationPrincipal CustomOAuth2User user,
		@RequestParam String fcmToken
	) {
		testService.testDiaryNoti(user.getUserId(), fcmToken);
		return Response.of(
			HttpStatus.OK,
			null,
			null
		);
	}

	@GetMapping("/test/invite")
	public Response<Void> testInvite(
		@AuthenticationPrincipal CustomOAuth2User user,
		@RequestParam String fcmToken,
		@RequestParam String planTitle
	) {
		testService.testInviteNoti(user.getUserId(), fcmToken, planTitle);
		return Response.of(
			HttpStatus.OK,
			null,
			null
		);
	}

	@Getter
	@RequiredArgsConstructor
	private enum ResponseHeader {

		FIREBASE_ADDED("토큰 추가에 성공했습니다.", HttpStatus.NO_CONTENT),
		TEST("테스트용 API입니다.", HttpStatus.NO_CONTENT),
		NOTI_READ("읽음 처리에 성공했습니다.", HttpStatus.NO_CONTENT)
		;

		private final String message;

		private final HttpStatus status;

	}
}
