package com.j10d207.tripeer.noti.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.FirebaseException;
import com.google.firebase.messaging.Message;
import com.j10d207.tripeer.noti.db.entity.Notification;
import com.j10d207.tripeer.noti.db.entity.NotificationTask;
import com.j10d207.tripeer.noti.db.firebase.FirebasePublisher;
import com.j10d207.tripeer.noti.db.firebase.MessageBody;
import com.j10d207.tripeer.noti.db.firebase.MessageBuilder;
import com.j10d207.tripeer.noti.db.repository.NotificationTaskRepository;
import com.j10d207.tripeer.user.service.UserService;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationTaskService {

	private final NotificationTaskRepository notificationTaskRepository;

	private final FirebaseTokenService firebaseTokenService;

	private final FirebasePublisher firebasePublisher;

	private final UserService userService;

	private final EntityManager em;


	@Transactional
	public void updateStateToSent(NotificationTask task) {
		if (!em.contains(task)) task = em.merge(task);
		task.toSENT();
	}

	@Transactional
	public List<NotificationTask> getScheduleNeedTasksForPlan(
		final Map<Long, List<String>> tokenMap,
		final Map<Long, Notification> notificationMap
	) {

		final List<NotificationTask> allTasks = tokenMap.keySet().stream()
			.flatMap(userId -> {
				final List<String> tokens = tokenMap.get(userId);
				return tokens.stream()
					.map(token -> NotificationTask.of(notificationMap.get(userId), token));
			})
			.toList();

		allTasks.stream()
			.filter(NotificationTask::isImmediately)
			.forEach(this::processingMessageTask);

		final List<NotificationTask> scheduleTasks = allTasks.stream()
			.filter(NotificationTask::isScheduled)
			.toList();

		notificationTaskRepository.saveAll(scheduleTasks);

		return scheduleTasks;
	}




	@Transactional
	public void processingMessageTask (NotificationTask task) {

		if(em.contains(task)) task = em.merge(task);

		final Long userId =  task.getNotification().getUserId();

		final Boolean isAllow = userService.getAllowNotificationById(userId);

		if (!isAllow) {
			log.info("Not allow Notification userId: {}", userId);
			return;
		}

		final Notification notification = task.getNotification();

		task.toSENT();

		final MessageBody messageBody = new MessageBody(notification.getTitle(), notification.getContent(), notification.getMsgType());
		try {
			final Message fcmMessage = MessageBuilder.toFirebaseMessage(messageBody, task.getTargetToken());
			firebasePublisher.sendFirebaseMessage(fcmMessage);
		} catch (FirebaseException e) {
			firebaseTokenService.invalidFirebaseHandler(task.getTargetToken());
		}
	}

	@Transactional
	public List<NotificationTask> getUnsentNotificationTasks() {
		return notificationTaskRepository.findAllWithUnsent();
	}
}
