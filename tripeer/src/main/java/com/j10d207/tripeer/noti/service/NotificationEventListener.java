package com.j10d207.tripeer.noti.service;

import java.time.LocalDate;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.j10d207.tripeer.plan.db.entity.PlanEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationEventListener {

	private final FCMTaskHandler fcmTaskHandler;

	@EventListener
	@Async
	public void CompletePlanEventListener(final CompletePlanEvent planEvent) {

		// TODO: Plan 시작 알림을 보내는 것과, 다른 하나는 Diary 쓰라는 알림
		final PlanEntity completionPlan = planEvent.getPlan();
		final LocalDate startDate = completionPlan.getStartDate();

		if (LocalDate.now().isAfter(startDate)) {
			log.debug("Listener 에서 스케쥴러 작동완료: {}", completionPlan.getTitle());
			fcmTaskHandler.handlePlanNoti(completionPlan, true);
		}
		if (LocalDate.now().isEqual(startDate)) {
			log.debug("Listener 에서 즉각 메시지 작동완료: {}", completionPlan.getTitle());
			fcmTaskHandler.handlePlanNoti(completionPlan, false);
		}

	}

	@EventListener
	@Async
	public void InviteCoworkerEventListener(final InviteCoworkerEvent inviteCoworkerEvent) {
		// TODO: 동료 초대 이벤트 헨들러

	}




}
