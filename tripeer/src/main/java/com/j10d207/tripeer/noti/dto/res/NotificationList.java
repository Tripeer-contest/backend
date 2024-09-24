package com.j10d207.tripeer.noti.dto.res;

import java.util.List;

import lombok.Builder;

@Builder
public record NotificationList(List<NotificationDto> list, boolean hasNext, int size) {


	public static NotificationList of(final List<NotificationDto> list, final int size) {
		if (list.size() > size) {
			return new NotificationList(list.subList(0, size), true, size);
		}
		return new NotificationList(list, false, list.size());
	}
}
