package com.j10d207.tripeer.noti.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.FirebaseException;
import com.google.firebase.messaging.Message;
import com.j10d207.tripeer.noti.db.entity.Notification;
import com.j10d207.tripeer.noti.db.entity.NotificationTask;
import com.j10d207.tripeer.noti.db.firebase.FirebasePublisher;
import com.j10d207.tripeer.noti.db.firebase.MessageBody;
import com.j10d207.tripeer.noti.db.firebase.MessageBuilder;
import com.j10d207.tripeer.noti.db.firebase.MessageType;
import com.j10d207.tripeer.noti.db.repository.NotificationTaskRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationTaskService {

	private final NotificationTaskRepository notificationTaskRepository;

	private final NotificationService notificationService;

	private final FirebasePublisher firebasePublisher;

	private final EntityManager em;

	public List<NotificationTask> createTasks(
		final String planTitle,
		final LocalDateTime startAt,
		final List<Notification> notifications,
		final MessageType type
	) {
		return notifications.stream().map(notification -> {
				final String nickname = notification.getUser().getNickname();
				final MessageBody body = MessageBuilder.getMessageBody(type, planTitle, nickname);
				return toNotificationTask(body, notification, startAt);
			}).toList();
	}

	private NotificationTask toNotificationTask(
		final MessageBody messageBody,
		final Notification notification,
		final LocalDateTime startAt
	) {
		return NotificationTask.of(messageBody, notification, startAt);
	}

	@Transactional
	public void saveTasks(final List<NotificationTask> tasks) {
		notificationTaskRepository.saveAll(tasks);
	}

	@Transactional
	public void updateStateToSent(final NotificationTask task) {
		final NotificationTask mergedTaskEntity = em.merge(task);
		mergedTaskEntity.toSENT();
	}


	@Transactional
	public void processingMessageTask (final NotificationTask task) {

		final Notification notification = task.getToken();
		final MessageBody messageBody = new MessageBody(task.getTitle(), task.getContent(), task.getMsgType());
		try {
			final Message fcmMessage = MessageBuilder.toFirebaseMessage(messageBody, notification.getToken());
			firebasePublisher.sendFirebaseMessage(fcmMessage);
		} catch (FirebaseException e) {
			notificationService.invalidFirebaseHandler(notification);
		}
	}

	@Transactional
	public List<NotificationTask> getUnsentNotificationTasks() {
		return notificationTaskRepository.findAllWithUnsent();
	}
}
