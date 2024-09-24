package com.j10d207.tripeer.noti.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.j10d207.tripeer.noti.db.entity.FirebaseToken;
import com.j10d207.tripeer.noti.db.entity.Notification;
import com.j10d207.tripeer.noti.db.entity.NotificationTask;
import com.j10d207.tripeer.noti.db.firebase.MessageBody;
import com.j10d207.tripeer.noti.db.firebase.MessageBuilder;
import com.j10d207.tripeer.noti.db.firebase.MessageType;
import com.j10d207.tripeer.plan.event.CompletePlanEvent;
import com.j10d207.tripeer.plan.event.CoworkerDto;
import com.j10d207.tripeer.plan.event.InviteCoworkerEvent;
import com.j10d207.tripeer.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationEventHandler implements ApplicationListener<ApplicationStartedEvent> {

	private final TaskScheduler scheduler;
	private final FirebaseTokenService firebaseTokenService;
	private final NotificationTaskService notificationTaskService;
	private final NotificationService notificationService;
	private final UserService userService;
	private static final int BASIC_NOTI_HOUR = 9;
	private static final int BASIC_NOTI_MINUTE = 0;
	private static final int NEXT_DAY = 1;
	private static final ZoneId SEOUL_TIMEZONE = ZoneId.of("Asia/Seoul");

	public NotificationEventHandler(
		@Qualifier("taskScheduler")
		TaskScheduler scheduler,
		FirebaseTokenService service,
		NotificationTaskService notificationTaskService,
		NotificationService notificationService,
		UserService userService
	) {
		this.scheduler = scheduler;
		this.firebaseTokenService = service;
		this.notificationTaskService = notificationTaskService;
		this.notificationService = notificationService;
		this.userService = userService;
	}

	public void handlePlanNoti(final CompletePlanEvent event, final boolean isScheduled) {

		final List<CoworkerDto> coworkers = event.getCoworkers();
		final List<Long> userIds = coworkers.stream().map(CoworkerDto::getId).toList();
		final String planTitle = event.getPlanTitle();
		final Map<Long, List<String>> tokenMap = firebaseTokenService.findAllNotificationByUsers(userIds);
		final LocalDateTime tripeerStartAt = event.getStartAt().atTime(BASIC_NOTI_HOUR, BASIC_NOTI_MINUTE);
		final LocalDateTime diaryStartAt = event.getEndAt().plusDays(NEXT_DAY).atTime(BASIC_NOTI_HOUR, BASIC_NOTI_MINUTE);

		handleTripeerNotification(tokenMap, coworkers, planTitle, tripeerStartAt);
		handleDiaryNotification(tokenMap, coworkers, planTitle, diaryStartAt);
	}

	private void handleDiaryNotification(
		final Map<Long, List<String>> tokenMap,
		final List<CoworkerDto> coworkers,
		final String planTitle,
		final LocalDateTime startAt
	) {
		final Map<Long, MessageBody> msgBodyMap = coworkers.stream()
			.collect(Collectors.toMap(
				CoworkerDto::getId,
				coworker -> {
					final String nickname = coworker.getNickname();
					return MessageBuilder.getMessageBody(MessageType.DIARY_SAVE, planTitle, nickname);
				}
			));

		final Map<Long, Notification> notificationMap = notificationService.getNotificationMapAfterSave(msgBodyMap, startAt, null);

		final List<NotificationTask> willSchedulingTasks = notificationTaskService.getScheduleNeedTasksForPlan(tokenMap, notificationMap);

		willSchedulingTasks.forEach(task -> {
			scheduler.schedule(toRunnableTask(task), toInstant(startAt));
		});
	}

	private void handleTripeerNotification(
		final Map<Long, List<String>> tokenMap,
		final List<CoworkerDto> coworkers,
		final String planTitle,
		final LocalDateTime startAt
	) {

		final Map<Long, MessageBody> msgBodyMap = coworkers.stream()
			.collect(Collectors.toMap(
				CoworkerDto::getId,
				coworker -> {
					final String nickname = coworker.getNickname();
					return MessageBuilder.getMessageBody(MessageType.TRIPEER_START, planTitle, nickname);
				}
			));

		final Map<Long, Notification> notificationMap = notificationService.getNotificationMapAfterSave(msgBodyMap, startAt, null);

		final List<NotificationTask> willSchedulingTasks = notificationTaskService.getScheduleNeedTasksForPlan(tokenMap, notificationMap);

		willSchedulingTasks.forEach(task -> {
			scheduler.schedule(toRunnableTask(task), toInstant(startAt));
		});
	}

	public Runnable toRunnableTask(final NotificationTask task) {
		return () -> {
			log.info("task execute: {}", task.getNotification().getTitle());
			notificationTaskService.updateStateToSent(task);
			notificationTaskService.processingMessageTask(task);
		};
	}

	public void handleInviteNoti(final InviteCoworkerEvent event) {

		final String planTitle = event.getPlanTitle();
		final CoworkerDto coworker = event.getInvitedCoworker();

		final MessageBody inviteMsgBody = MessageBuilder.getMessageBody(MessageType.USER_INVITED, planTitle, coworker.getNickname());

		final Notification notification = notificationService.getNotificaitonAfterSave(inviteMsgBody, coworker.getId(), LocalDateTime.now(), event.getPlanId());

		final List<String> firebaseTokens = firebaseTokenService.findAllNotificationByUser(coworker.getId());

		firebaseTokens.forEach(token -> {
			notificationTaskService.processingMessageTask(NotificationTask.of(notification, token));
		});

	}


	private Instant toInstant(final LocalDateTime notificateDateTime) {
		return notificateDateTime.atZone(SEOUL_TIMEZONE).toInstant();
	}

	@Override
	@Transactional
	public void onApplicationEvent(ApplicationStartedEvent event) {
		// TODO: 어플리케이션 빈 초기화 이후 실행 직전에 감지되는 이벤트
		log.info("application started event 감지");
		final List<NotificationTask> unsentTasks = notificationTaskService.getUnsentNotificationTasks();

		unsentTasks.forEach(task -> {
				if(task.isImmediately()) {
					notificationTaskService.updateStateToSent(task);
					notificationTaskService.processingMessageTask(task);
				}
				if(task.isScheduled()) {
					log.info("scheduling task: {}, at: {}", task.getNotification().getId(), task.getNotification().getStartAt());
					scheduler.schedule(toRunnableTask(task), toInstant(task.getNotification().getStartAt()));
				}
			});
	}
}
