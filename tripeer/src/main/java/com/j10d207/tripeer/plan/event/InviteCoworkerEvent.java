package com.j10d207.tripeer.plan.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
public class InviteCoworkerEvent {

	private final CoworkerDto invitor;

	private final CoworkerDto invitedCoworker;

	private final String planTitle;

	private final Long planId;
}
