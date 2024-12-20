package com.j10d207.tripeer.noti.listener;

import java.time.LocalDate;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.j10d207.tripeer.noti.service.NotificationEventHandler;
import com.j10d207.tripeer.plan.event.CompletePlanEvent;
import com.j10d207.tripeer.plan.event.InviteCoworkerEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationEventListener {

	private final NotificationEventHandler notificationEventHandler;

	@EventListener
	@Async
	public void completePlanEventListener(final CompletePlanEvent planEvent) {

		// TODO: Plan 시작 알림을 보내는 것과, 다른 하나는 Diary 쓰라는 알림
		log.info("planEvent 도착: {}", planEvent.getPlanTitle());
		notificationEventHandler.handlePlanNoti(planEvent, true);
	}

	@EventListener
	@Async
	public void inviteCoworkerEventListener(final InviteCoworkerEvent inviteCoworkerEvent) {

		// TODO: 동료 초대 이벤트 헨들러
		notificationEventHandler.handleInviteNoti(inviteCoworkerEvent);

	}




}
