package com.j10d207.tripeer.noti.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.j10d207.tripeer.noti.db.entity.Notification;
import com.j10d207.tripeer.noti.db.entity.NotificationTask;
import com.j10d207.tripeer.noti.db.firebase.MessageType;
import com.j10d207.tripeer.plan.db.entity.PlanEntity;
import com.j10d207.tripeer.user.db.entity.CoworkerEntity;
import com.j10d207.tripeer.user.db.entity.UserEntity;

@Service
public class FCMTaskHandler {

	private final TaskScheduler scheduler;
	private final NotificationService notificationService;
	private final NotificationTaskService notificationTaskService;

	private static final int BASIC_NOTI_HOUR = 9;
	private static final int BASIC_NOTI_MINUTE = 0;
	private static final int NEXT_DAY = 1;

	private static final ZoneId zoneId = ZoneId.of("Asia/Seoul");

	public FCMTaskHandler(TaskScheduler scheduler, NotificationService service, NotificationTaskService notificationTaskService) {
		this.scheduler = scheduler;
		this.notificationService = service;
		this.notificationTaskService = notificationTaskService;
	}

	@Transactional
	public void handlePlanNoti(final PlanEntity completionPlan, final boolean isScheduled) {
		final List<UserEntity> coworkers = completionPlan.getCoworkerList().stream().map(CoworkerEntity::getUser).toList();
		final List<Notification> notifications = notificationService.findAllNotificationByUser(coworkers);
		final String planTitle = completionPlan.getTitle();
		final LocalDateTime forTripeer = completionPlan.getStartDate().atTime(BASIC_NOTI_HOUR, BASIC_NOTI_MINUTE);
		final LocalDateTime forDiary = completionPlan.getEndDate().plusDays(NEXT_DAY).atTime(BASIC_NOTI_HOUR, BASIC_NOTI_MINUTE);
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


	private Runnable getTask(final NotificationTask task) {
		return () -> notificationTaskService.processingMessageTask(task);
	}

	private Instant toInstant(final LocalDateTime notificateDateTime) {
		return notificateDateTime.atZone(zoneId).toInstant();
	}


}
