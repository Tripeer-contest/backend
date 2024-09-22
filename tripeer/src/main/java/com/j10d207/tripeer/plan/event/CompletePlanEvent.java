package com.j10d207.tripeer.plan.event;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@AllArgsConstructor
@Getter
public class CompletePlanEvent {

	private LocalDate startAt;

	private LocalDate endAt;

	private String planTitle;

	private List<CoworkerDto> coworkers;
}
