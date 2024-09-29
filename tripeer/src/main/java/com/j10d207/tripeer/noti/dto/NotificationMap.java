package com.j10d207.tripeer.noti.dto;

import java.util.Map;
import java.util.Optional;

public class NotificationMap {

	private final Map<Long, Long> notificationMap;

	public NotificationMap(final Map<Long, Long> notificationMap) {
		this.notificationMap = notificationMap;
	}

	public Optional<Long> getNotificationId(final Long userId) {
		return Optional.ofNullable(notificationMap.get(userId));
	}
}
