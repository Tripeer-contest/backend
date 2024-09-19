package com.j10d207.tripeer.noti.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.j10d207.tripeer.plan.db.repository.PlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationEventPublisher {

	private final ApplicationEventPublisher publisher;

	private final PlanRepository planRepository;

	public void publish() {
		publisher.publishEvent(new CompletePlanEvent(planRepository.findByPlanId(138)));
	}
}
