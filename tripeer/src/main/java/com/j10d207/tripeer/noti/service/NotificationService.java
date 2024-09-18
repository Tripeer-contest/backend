package com.j10d207.tripeer.noti.service;

import org.springframework.stereotype.Service;

import com.google.firebase.FirebaseException;
import com.j10d207.tripeer.noti.db.entity.Notification;
import com.j10d207.tripeer.noti.db.firebase.FirebasePublisher;
import com.j10d207.tripeer.noti.db.firebase.MessageType;
import com.j10d207.tripeer.noti.db.repository.NotificationRepository;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import com.j10d207.tripeer.user.db.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: 김회창
 *
 * 알림 처리 비즈니스 서비스
 * 외부 자원을 조합하여 도메인별 처리 순서만 맞춤
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final NotificationRepository notificationRepository;

	private final FirebasePublisher firebasePublisher;

	private final UserRepository userRepository;


	/**
	 * @author: 김회창
	 *
	 * <p>
	 *      해당하는 유저에 새로운 Notification 엔티티를 사용하여 새로운 토큰을 DB에 저장
	 * </p>
	 *
	 * @param userId: 		 토큰이 추가될 유저 식별번호
	 * @param firebaseToken: requestParam firebaseToken
	 * */
	@Transactional
	public void addFirebaseToken(final Long userId, final String firebaseToken) {
		UserEntity user = userRepository.findByUserId(userId);
		Notification notification = Notification.of(user, firebaseToken);
		notificationRepository.save(notification);
	}

	/**
	 * @author: 김회창
	 *
	 * <p>
	 *     여행 시작을 알리는 알림을 Firebase 외부 서비스를 통해 발행
	 * </p>
	 *
	 * @param userId: 		 토큰이 추가될 유저 식별번호
	 */

	@Transactional
	public void publishTripeerStart(final Long userId) {

	}

	/**
	 * @author: 김회창
	 *
	 * <p>
	 *     다이어리 저장 알림을 Firbase 외부 서비스를 통해 발행
	 * </p>
	 *
	 * @param userId: 		 토큰이 추가될 유저 식별번호
	 */

	@Transactional
	public void publishDiarySave(final Long userId) {

	}

	/**
	 * @author: 김회창
	 *
	 * <p>
	 *     사용자 초대 알림을 Firbase 외부 서비스를 통해 발행
	 * </p>
	 *
	 * @param userId: 		 토큰이 추가될 유저 식별번호
	 */

	@Transactional
	public void publishInvited(final Long userId) {

	}


}
