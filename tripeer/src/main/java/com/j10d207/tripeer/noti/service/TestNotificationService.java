package com.j10d207.tripeer.noti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.google.firebase.FirebaseException;
import com.google.firebase.messaging.Message;
import com.j10d207.tripeer.noti.db.entity.FirebaseToken;
import com.j10d207.tripeer.noti.db.firebase.FirebasePublisher;
import com.j10d207.tripeer.noti.db.firebase.MessageBody;
import com.j10d207.tripeer.noti.db.firebase.MessageBuilder;
import com.j10d207.tripeer.noti.db.firebase.MessageType;
import com.j10d207.tripeer.noti.db.repository.FirebaseTokenRepository;
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

	private final UserRepository userRepository;


	public void testTripeerNoti(final long userId, String token, String planTitle) {
		final UserEntity user = userRepository.findByUserId(userId);
		final String userNickname = user.getNickname();
		List<String> firebaseTokens = firebaseTokenRepository.findAllAvailableByUser(List.of(user))
			.stream().map(FirebaseToken::getToken).toList();
		List<String> allTokens = new ArrayList<>(firebaseTokens);
		if (token != null && !token.isBlank()) {
			allTokens.add(token);
		}
		List<MessageBody> messageBodies = allTokens.stream().map((fcmToken) ->
			MessageBuilder.getMessageBody(MessageType.TRIPEER_START, planTitle, userNickname)
		).toList();
		log.info("token list sz: {}, msg body list sz: {}", allTokens.size(),messageBodies.size());
		IntStream.range(0, allTokens.size())
			.boxed()
			.forEach(idx -> {
				Message message = MessageBuilder.toFirebaseMessage(messageBodies.get(idx), allTokens.get(idx));
				try {
					firebasePublisher.sendFirebaseMessage(message);
				} catch (FirebaseException e) {

				}
			});
	}

	public void testDiaryNoti(final long userId, String token) {
		final UserEntity user = userRepository.findByUserId(userId);
		final String userNickname = user.getNickname();
		List<String> firebaseTokens = firebaseTokenRepository.findAllAvailableByUser(List.of(user))
			.stream().map(FirebaseToken::getToken).toList();
		List<String> allTokens = new ArrayList<>(firebaseTokens);
		if (token != null && !token.isBlank()) {
			allTokens.add(token);
		}
		List<MessageBody> messageBodies = allTokens.stream().map((fcmToken) ->
			MessageBuilder.getMessageBody(MessageType.DIARY_SAVE, null, userNickname)
		).toList();
		log.info("token list sz: {}, msg body list sz: {}", allTokens.size(),messageBodies.size());
		IntStream.range(0, allTokens.size())
			.boxed()
			.forEach(idx -> {
				Message message = MessageBuilder.toFirebaseMessage(messageBodies.get(idx), allTokens.get(idx));
				try {
					firebasePublisher.sendFirebaseMessage(message);
				} catch (FirebaseException e) {
				}
			});
	}

	public void testInviteNoti(final long userId, String token, String planTitle) {
		final UserEntity user = userRepository.findByUserId(userId);
		final String userNickname = user.getNickname();
		List<String> firebaseTokens = firebaseTokenRepository.findAllAvailableByUser(List.of(user))
			.stream().map(FirebaseToken::getToken).toList();
		List<String> allTokens = new ArrayList<>(firebaseTokens);
		if (token != null && !token.isBlank()) {
			allTokens.add(token);
		}
		List<MessageBody> messageBodies = allTokens.stream().map((fcmToken) ->
			MessageBuilder.getMessageBody(MessageType.USER_INVITED, planTitle, userNickname)
		).toList();
		log.info("token list sz: {}, msg body list sz: {}", allTokens.size(),messageBodies.size());
		IntStream.range(0, allTokens.size())
			.boxed()
			.forEach(idx -> {
				Message message = MessageBuilder.toFirebaseMessage(messageBodies.get(idx), allTokens.get(idx));
				try {
					firebasePublisher.sendFirebaseMessage(message);
				} catch (FirebaseException e) {
				}
			});
	}
}
