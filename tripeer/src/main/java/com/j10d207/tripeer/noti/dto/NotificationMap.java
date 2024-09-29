package com.j10d207.tripeer.noti.dto;

import java.util.Map;
import java.util.Optional;

public class NotificationMap {

	private final Map<Long, NotificationDto> notificationMap;

	public NotificationMap(final Map<Long, NotificationDto> notificationMap) {
		this.notificationMap = Map.copyOf(notificationMap);
	}

	public Optional<NotificationDto> getNotification(final Long userId) {
		return Optional.ofNullable(notificationMap.get(userId));
	}
}
