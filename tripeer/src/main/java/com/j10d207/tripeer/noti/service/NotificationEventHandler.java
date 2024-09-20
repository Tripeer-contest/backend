package com.j10d207.tripeer.noti.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import com.j10d207.tripeer.noti.db.entity.Notification;
import com.j10d207.tripeer.noti.db.entity.NotificationTask;
import com.j10d207.tripeer.noti.db.firebase.MessageType;
import com.j10d207.tripeer.plan.event.CompletePlanEvent;
import com.j10d207.tripeer.plan.event.InviteCoworkerEvent;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationEventHandler implements ApplicationListener<ApplicationStartedEvent> {

	private final TaskScheduler scheduler;
	private final NotificationService notificationService;
	private final NotificationTaskService notificationTaskService;
	private static final int BASIC_NOTI_HOUR = 9;
	private static final int BASIC_NOTI_MINUTE = 0;
	private static final int NEXT_DAY = 1;
	private static final ZoneId SEOUL_TIMEZONE = ZoneId.of("Asia/Seoul");

	public NotificationEventHandler(
		@Qualifier("taskScheduler")
		TaskScheduler scheduler,
		NotificationService service,
		NotificationTaskService notificationTaskService,
		PlatformTransactionManager txManager
	) {
		this.scheduler = scheduler;
		this.notificationService = service;
		this.notificationTaskService = notificationTaskService;
	}

	@Transactional
	public void handlePlanNoti(final CompletePlanEvent event, final boolean isScheduled) {

		final List<Notification> notifications = notificationService.findAllNotificationByUsers(event.getCoworkers());
		final String planTitle = event.getPlanTitle();
		final LocalDateTime forTripeer = event.getStartAt().atTime(BASIC_NOTI_HOUR, BASIC_NOTI_MINUTE);
		final LocalDateTime forDiary = event.getEndAt().plusDays(NEXT_DAY).atTime(BASIC_NOTI_HOUR, BASIC_NOTI_MINUTE);
		final List<NotificationTask> tripeerStartTasks = notificationTaskService.createTasks(planTitle, forTripeer, notifications, MessageType.TRIPEER_START);
		final List<NotificationTask> diaryTasks = notificationTaskService.createTasks(planTitle, forDiary, notifications, MessageType.DIARY_SAVE);

		notificationTaskService.saveTasks(diaryTasks);

		diaryTasks.forEach(task -> {
			scheduler.schedule(getTask(task), toInstant(task.getStartAt()));
		});

		if (!isScheduled) {
			tripeerStartTasks.forEach(notificationTaskService::processingMessageTask);
			return;
		}

		notificationTaskService.saveTasks(tripeerStartTasks);
		tripeerStartTasks.forEach(task -> {
			scheduler.schedule(getTask(task), toInstant(task.getStartAt()));
		});
	}

	@Transactional
	public void handleInviteNoti(final InviteCoworkerEvent event) {

		final String planTitle = event.getPlanTitle();
		final List<Notification> notifications = notificationService.findAllNotificationByUser(event.getInvitedCoworker());
		final List<NotificationTask> tasks = notificationTaskService.createTasks(planTitle, null, notifications, MessageType.USER_INVITED);
		tasks.forEach(notificationTaskService::processingMessageTask);
	}


	public Runnable getTask(final NotificationTask task) {
		return () -> {
			log.info("task 실행: {}", task.getTitle());
			notificationTaskService.updateStateToSent(task);
			notificationTaskService.processingMessageTask(task);
		};
	}

	private Instant toInstant(final LocalDateTime notificateDateTime) {
		return notificateDateTime.atZone(SEOUL_TIMEZONE).toInstant();
	}

	@Override
	@Transactional
	public void onApplicationEvent(ApplicationStartedEvent event) {
		// TODO: 어플리케이션 빈 초기화 이후 실행 직전에 감지되는 이벤트
		log.info("application started event 감지");
		final LocalDateTime nowTime = LocalDateTime.now();
		final LocalDate nowDate = nowTime.toLocalDate();
		final List<NotificationTask> unsentTasks = notificationTaskService.getUnsentNotificationTasks();

		unsentTasks.forEach(unsentTask -> {
			final LocalDateTime unsentDateTime = unsentTask.getStartAt();
			final LocalDate sentDay = unsentDateTime.toLocalDate();
			if (nowDate.isEqual(sentDay) || nowDate.isAfter(sentDay)) {
				log.info("보내어지지 못한 테스크 즉각처리: {}", unsentTask.getTitle());
				notificationTaskService.updateStateToSent(unsentTask);
				notificationTaskService.processingMessageTask(unsentTask);
			}
			if (nowDate.isBefore(sentDay)) {
				log.info("보내어지지 못한 테스크 스케쥴링: {}", unsentTask.getTitle());
				scheduler.schedule(getTask(unsentTask), toInstant(unsentDateTime));
			}
		});
	}


}
