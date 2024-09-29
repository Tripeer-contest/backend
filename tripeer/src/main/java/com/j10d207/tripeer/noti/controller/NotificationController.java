package com.j10d207.tripeer.noti.controller;


import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.j10d207.tripeer.noti.dto.res.NotificationList;
import com.j10d207.tripeer.noti.service.FirebaseTokenService;
import com.j10d207.tripeer.noti.service.NotificationEventTestPublisher;
import com.j10d207.tripeer.noti.service.NotificationService;
import com.j10d207.tripeer.noti.service.NotificationTaskService;
import com.j10d207.tripeer.noti.service.TestNotificationService;
import com.j10d207.tripeer.response.Response;
import com.j10d207.tripeer.user.dto.res.CustomOAuth2User;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
	private final NotificationEventTestPublisher publisher;

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
		@PathVariable("notificationId") @Min(value = 1, message = "0보다 커야 합니다.") Long id
	) {
		notificationService.updateStateToRead(id);
		return Response.of(
			ResponseHeader.NOTI_READ.getStatus(),
			ResponseHeader.NOTI_READ.getMessage(),
			null
		);
	}

	/**
	 * @author: 김회창
	 *
	 * <p>
	 *     해당하는 유저의 SENT 상태의 알림 목록을 무한스크롤 형태로 lastId보다 작으면서 size 개수만큼 조회 한다.
	 * </p>
	 *
	 * @param user: 로그인 된 유저
	 * @param lastId: 마지막 번호
	 * @param size: 목록의 아이템 개수
	 * @status: 성공시 200 OK
	 * @body: 	NotificationList
	 *
	 */

	@GetMapping
	public Response<NotificationList> getNotificationList(
		@AuthenticationPrincipal CustomOAuth2User user,
		@RequestParam(name = "lastid", required = false) @Min(value = 1, message = "lastid는 0보다 커야 합니다.") Long lastId,
		@RequestParam(name = "size", defaultValue = "20", required = false)
			@Max(value = 40, message = "size는 10 ~ 40 까지 유효합니다. 기본값은 20입니다.")
			@Min(value = 10, message = "size는 10 ~ 40 까지 유효합니다. 기본값은 20입니다.") int size
	) {
		final NotificationList responseBody = notificationService.findAllWithReceiveByUser(user.getUserId(), Optional.ofNullable(lastId), size);

		return Response.of(
			ResponseHeader.NOTI_LIST.getStatus(),
			ResponseHeader.NOTI_LIST.getMessage(),
			responseBody
		);
	}


	@GetMapping("/test/tripeer")
	public Response<Void> testTripeerStart(
		@AuthenticationPrincipal CustomOAuth2User user,
		@RequestParam String planTitle
	) {
		testService.testTripeerNoti(user.getUserId(), planTitle);
		return Response.of(
			HttpStatus.OK,
			null,
			null
		);
	}

	@GetMapping("/test/diary")
	public Response<Void> testDiarySave(
		@AuthenticationPrincipal CustomOAuth2User user
	) {
		publisher.publish();
		// testService.testDiaryNoti(user.getUserId());
		return Response.of(
			HttpStatus.OK,
			null,
			null
		);
	}

	@GetMapping("/test/invite")
	public Response<Void> testInvite(
		@AuthenticationPrincipal CustomOAuth2User user,
		@RequestParam String planTitle,
		@RequestParam Long planId
	) {
		testService.testInviteNoti(user.getUserId(), planTitle, planId);
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
		NOTI_READ("읽음 처리에 성공했습니다.", HttpStatus.NO_CONTENT),
		NOTI_LIST("조회에 성공하였습니다.", HttpStatus.OK)
		;

		private final String message;

		private final HttpStatus status;

	}
}
