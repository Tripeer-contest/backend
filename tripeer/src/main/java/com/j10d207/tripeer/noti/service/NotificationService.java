package com.j10d207.tripeer.noti.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.FirebaseException;
import com.google.firebase.messaging.Message;
import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.noti.db.entity.FirebaseToken;
import com.j10d207.tripeer.noti.db.entity.Notification;
import com.j10d207.tripeer.noti.db.firebase.FirebasePublisher;
import com.j10d207.tripeer.noti.db.firebase.MessageBody;
import com.j10d207.tripeer.noti.db.firebase.MessageBuilder;
import com.j10d207.tripeer.noti.db.firebase.MessageType;
import com.j10d207.tripeer.noti.db.repository.NotificationRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final NotificationRepository notificationRepository;

	private final FirebaseTokenService firebaseTokenService;

	private final FirebasePublisher firebasePublisher;

	private final EntityManager em;

	public List<Notification> createTasks(
		final String planTitle,
		final LocalDateTime startAt,
		final List<FirebaseToken> firebaseTokens,
		final MessageType type
	) {
		return firebaseTokens.stream().map(notification -> {
				final String nickname = notification.getUser().getNickname();
				final MessageBody body = MessageBuilder.getMessageBody(type, planTitle, nickname);
				return toNotificationTask(body, notification, startAt);
			}).toList();
	}

	private Notification toNotificationTask(
		final MessageBody messageBody,
		final FirebaseToken firebaseToken,
		final LocalDateTime startAt
	) {
		return Notification.of(messageBody, firebaseToken, startAt);
	}

	@Transactional
	public void saveTasks(final List<Notification> tasks) {
		notificationRepository.saveAll(tasks);
	}

	@Transactional
	public void updateStateToSent(Notification task) {
		if (!em.contains(task)) task = em.merge(task);
		task.toSENT();
	}

	@Transactional
	public void updateStateToRead(final Long id) {
		final Optional<Notification> notification = notificationRepository.findById(id);
		notification.ifPresentOrElse(
			Notification::toREAD,
			() -> {
				throw new CustomException(ErrorCode.NOT_FOUND_NOTI);
			}
		);
	}


	@Transactional
	public void processingMessageTask (final Notification task) {

		final FirebaseToken firebaseToken = task.getToken();
		final MessageBody messageBody = new MessageBody(task.getTitle(), task.getContent(), task.getMsgType());
		try {
			final Message fcmMessage = MessageBuilder.toFirebaseMessage(messageBody, firebaseToken.getToken());
			firebasePublisher.sendFirebaseMessage(fcmMessage);
		} catch (FirebaseException e) {
			firebaseTokenService.invalidFirebaseHandler(firebaseToken);
		}
	}

	@Transactional
	public List<Notification> getUnsentNotificationTasks() {
		return notificationRepository.findAllWithUnsent();
	}
}
