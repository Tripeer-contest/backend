package com.j10d207.tripeer.noti.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.j10d207.tripeer.noti.db.entity.FirebaseToken;
import com.j10d207.tripeer.noti.db.repository.FirebaseTokenRepository;
import com.j10d207.tripeer.plan.event.CoworkerDto;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import com.j10d207.tripeer.user.db.repository.UserRepository;

import jakarta.persistence.EntityManager;
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
public class FirebaseTokenService {

	private final FirebaseTokenRepository firebaseTokenRepository;

	private final UserRepository userRepository;

	private final EntityManager em;

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
		final UserEntity user = userRepository.findByUserId(userId);
		final List<FirebaseToken> hasTokens = firebaseTokenRepository.findAllByUser(List.of(user));
		hasTokens.stream()
			.filter(hasToken -> hasToken.getToken().equals(firebaseToken))
			.findFirst()
			.ifPresentOrElse(
				FirebaseToken::unMark,
				() -> {
					final FirebaseToken notification = FirebaseToken.of(user, firebaseToken);
					firebaseTokenRepository.save(notification);
				}
			);
	}


	@Transactional
	public Map<Long, List<String>> findAllNotificationByUsers(final List<Long> userIds) {
		final List<UserEntity> users = userRepository.findAllById(userIds);
		final List<FirebaseToken> tokens = firebaseTokenRepository.findAllAvailableByUser(users);
		return tokens.stream()
			.collect(Collectors.groupingBy(
				token -> token.getUser().getUserId(),
				Collectors.mapping(FirebaseToken::getToken, Collectors.toList())
			));
	}


	@Transactional
	public List<String> findAllNotificationByUser(final Long userId) {
		final UserEntity user = userRepository.findByUserId(userId);
		return firebaseTokenRepository.findAllAvailableByUser(List.of(user)).stream()
			.map(FirebaseToken::getToken)
			.toList();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void invalidFirebaseHandler(final String firebaseToken) {
		final Optional<FirebaseToken> token = firebaseTokenRepository.findByToken(firebaseToken);
		token.ifPresent(FirebaseToken::mark);
	}
}
