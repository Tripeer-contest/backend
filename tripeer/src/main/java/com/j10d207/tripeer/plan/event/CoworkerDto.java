package com.j10d207.tripeer.plan.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Builder
@AllArgsConstructor
@Getter
public class CoworkerDto {

	private Long id;

	private String nickname;
}
