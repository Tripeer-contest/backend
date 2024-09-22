package com.j10d207.tripeer.noti.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.j10d207.tripeer.plan.db.repository.PlanRepository;
import com.j10d207.tripeer.plan.event.CoworkerDto;
import com.j10d207.tripeer.plan.event.InviteCoworkerEvent;
import com.j10d207.tripeer.user.db.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationEventTestPublisher {

	private final ApplicationEventPublisher publisher;

	public void publish() {
		publisher.publishEvent(InviteCoworkerEvent.builder()
			.planTitle("여행 제목")
			.invitedCoworker(CoworkerDto.builder()
				.id(75L)
				.nickname("김회창")
				.build())
			.build());
	}
}
