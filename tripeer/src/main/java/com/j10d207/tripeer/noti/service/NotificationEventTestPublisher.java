package com.j10d207.tripeer.noti.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.j10d207.tripeer.plan.db.repository.PlanRepository;
import com.j10d207.tripeer.plan.event.CompletePlanEvent;
import com.j10d207.tripeer.plan.event.CoworkerDto;
import com.j10d207.tripeer.plan.event.InviteCoworkerEvent;
import com.j10d207.tripeer.user.db.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationEventTestPublisher {

	private final ApplicationEventPublisher publisher;

	public void publish() {
		publisher.publishEvent(CompletePlanEvent.builder()
			.planTitle("내일 여행여행 테스트")
				.coworkers(List.of(new CoworkerDto(75L, "김회창")))
				.startAt(LocalDate.now().plusDays(1L))
				.endAt(LocalDate.now().plusDays(2L))
			.build());
	}
}
