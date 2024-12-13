package com.j10d207.tripeer.noti.dto.res;

import java.time.LocalDate;

import com.j10d207.tripeer.noti.db.firebase.MessageType;

import lombok.Builder;

@Builder
public record NotificationDto(Long id, MessageType type, String title, String content, LocalDate date, Long targetId) {


	public static NotificationDto of(final Long id, final MessageType type, final String title, final String content, final LocalDate date, final Long targetId) {
		return new NotificationDto(id, type, title, content, date, targetId);
	}
}
