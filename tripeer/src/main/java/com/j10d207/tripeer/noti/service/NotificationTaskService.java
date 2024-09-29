package com.j10d207.tripeer.noti.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.noti.db.entity.FirebaseToken;
import com.j10d207.tripeer.noti.db.firebase.MessageType;
import com.j10d207.tripeer.noti.dto.NotificationDto;
import com.j10d207.tripeer.noti.dto.NotificationMap;
import com.j10d207.tripeer.noti.dto.Token;
import com.j10d207.tripeer.noti.dto.TokenMap;
import com.j10d207.tripeer.noti.mapper.FirebaseTokenMapper;
import com.j10d207.tripeer.noti.mapper.NotificationMapper;
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
	public void updateStateToSent(final Long taskId) {
		final Optional<NotificationTask> optionalTask = notificationTaskRepository.findById(taskId);
		final NotificationTask task = optionalTask.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_NOTI_TASK));
		task.toSENT();
	}

	@Transactional
	public List<Long> getScheduleNeedTasksForPlan(
		final TokenMap tokenMap,
		final NotificationMap notificationMap
	) {

		final List<NotificationTask> allTasks = tokenMap.getUserIds().stream()
						.map(userId -> notificationMap.getNotification(userId)
								.map(notification -> {
									return tokenMap.getTokens(userId).stream()
											.map(token -> {
												log.info("token: {}", token.firebaseToken());
												return NotificationTask.of(notification, token);
											})
											.collect(Collectors.toList());
								})
								.orElseGet(Collections::emptyList)
						)
				.flatMap(List::stream)
				.toList();


		allTasks.stream()
			.filter(NotificationTask::isImmediately)
			.forEach(this::processingImmediately);

		final List<NotificationTask> scheduleTasks = allTasks.stream()
			.filter(NotificationTask::isScheduled)
			.toList();

		log.info("tasks: {}: ", allTasks);

		notificationTaskRepository.saveAll(scheduleTasks);

		return scheduleTasks.stream()
				.map(NotificationTask::getId)
				.toList();
	}

	private void processingImmediately(final NotificationTask entity) {


		log.info("notification: {}", entity.getNotification().getTitle());
		final NotificationDto notificationDto = NotificationMapper.toNotificationDto(entity.getNotification());
		final Token tokenDto = FirebaseTokenMapper.toTokenDto(entity.getTargetToken());
		processingImmediately(notificationDto, tokenDto);
	}

	public void processingImmediately(final NotificationDto notification, final Token token) {

		final MessageBody messageBody = new MessageBody(notification.title(), notification.content(), MessageType.valueOf(notification.msgType()));
		final Boolean isAllow = userService.getAllowNotificationById(notification.userId());
		if (!isAllow) {
			log.info("Not allow Notification userId: {}", notification.userId());
			return;
		}
		try {
			log.info("token: {}", token);
			final Message fcmMessage = MessageBuilder.toFirebaseMessage(messageBody, token);
			firebasePublisher.sendFirebaseMessage(fcmMessage);
		} catch (FirebaseException e) {
			firebaseTokenService.invalidFirebaseHandler(token);
		}
	}

	@Transactional
	public void processingMessageTask(final Long taskId) {

		final Optional<NotificationTask> optionalTask = notificationTaskRepository.findById(taskId);
		final NotificationTask task = optionalTask.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_NOTI_TASK));

		final Long userId =  task.getNotification().getUserId();

		final Boolean isAllow = userService.getAllowNotificationById(userId);

		if (!isAllow) {
			log.info("Not allow Notification userId: {}", userId);
			return;
		}

		final Notification notification = task.getNotification();

		task.toSENT();

		final MessageBody messageBody = new MessageBody(notification.getTitle(), notification.getContent(), notification.getMsgType());
		final Token token = FirebaseTokenMapper.toTokenDto(task.getTargetToken());
		try {
			final Message fcmMessage = MessageBuilder.toFirebaseMessage(messageBody, token);
			firebasePublisher.sendFirebaseMessage(fcmMessage);
		} catch (FirebaseException e) {
			firebaseTokenService.invalidFirebaseHandler(token);
		}
	}

	@Transactional
	public List<NotificationTask> getUnsentNotificationTasks() {
		return notificationTaskRepository.findAllWithUnsent();
	}
}
