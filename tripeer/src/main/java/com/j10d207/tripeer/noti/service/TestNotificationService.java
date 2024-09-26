package com.j10d207.tripeer.noti.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.google.firebase.FirebaseException;
import com.google.firebase.messaging.Message;
import com.j10d207.tripeer.noti.db.entity.FirebaseToken;
import com.j10d207.tripeer.noti.db.entity.Notification;
import com.j10d207.tripeer.noti.db.firebase.FirebasePublisher;
import com.j10d207.tripeer.noti.db.firebase.MessageBody;
import com.j10d207.tripeer.noti.db.firebase.MessageBuilder;
import com.j10d207.tripeer.noti.db.firebase.MessageType;
import com.j10d207.tripeer.noti.db.repository.FirebaseTokenRepository;
import com.j10d207.tripeer.noti.db.repository.NotificationRepository;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import com.j10d207.tripeer.user.db.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestNotificationService {

	private final FirebasePublisher firebasePublisher;

	private final FirebaseTokenRepository firebaseTokenRepository;

	private final FirebaseTokenService firebaseTokenService;

	private final NotificationRepository notificationRepository;

	private final UserRepository userRepository;


	public void testTripeerNoti(final long userId, String planTitle) {
		final UserEntity user = userRepository.findByUserId(userId);
		final String userNickname = user.getNickname();
		List<FirebaseToken> firebaseTokens = firebaseTokenRepository.findAllAvailableByUser(List.of(user));

		MessageBody msgBody = MessageBuilder.getMessageBody(MessageType.TRIPEER_START, planTitle, userNickname);

		firebaseTokens.forEach(token -> {
			try {
				firebasePublisher.sendFirebaseMessage(MessageBuilder.toFirebaseMessage(msgBody, token.getToken()));
			} catch (FirebaseException e) {
				firebaseTokenService.invalidFirebaseHandler(token.getToken(), userId);
			}

		});
		notificationRepository.save(Notification.of(msgBody, userId, LocalDateTime.now(), null));


	}

	public void testDiaryNoti(final long userId) {
		final UserEntity user = userRepository.findByUserId(userId);
		final String userNickname = user.getNickname();
		List<FirebaseToken> firebaseTokens = firebaseTokenRepository.findAllAvailableByUser(List.of(user));

		MessageBody msgBody = MessageBuilder.getMessageBody(MessageType.DIARY_SAVE, null, userNickname);


		firebaseTokens.forEach(token -> {
			try {
				firebasePublisher.sendFirebaseMessage(MessageBuilder.toFirebaseMessage(msgBody, token.getToken()));
			} catch (FirebaseException e) {
				firebaseTokenService.invalidFirebaseHandler(token.getToken(), userId);
			}

		});
		notificationRepository.save(Notification.of(msgBody, userId, LocalDateTime.now(), null));
	}

	public void testInviteNoti(final long userId,String planTitle, Long planId) {
		final UserEntity user = userRepository.findByUserId(userId);
		final String userNickname = user.getNickname();
		List<FirebaseToken> firebaseTokens = firebaseTokenRepository.findAllAvailableByUser(List.of(user));

		MessageBody msgBody = MessageBuilder.getMessageBody(MessageType.USER_INVITED, planTitle, userNickname);


		firebaseTokens.forEach(token -> {
			try {
				log.info("target token : {}", token.getToken());
				firebasePublisher.sendFirebaseMessage(MessageBuilder.toFirebaseMessage(msgBody, token.getToken()));
			} catch (FirebaseException e) {
				firebaseTokenService.invalidFirebaseHandler(token.getToken(), token.getUser().getUserId());
			}
		});
		notificationRepository.save(Notification.of(msgBody, userId, LocalDateTime.now(), planId));
	}
}
