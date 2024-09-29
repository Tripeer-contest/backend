package com.j10d207.tripeer.noti.mapper;

import com.j10d207.tripeer.noti.db.entity.Notification;
import com.j10d207.tripeer.noti.dto.NotificationDto;
import com.j10d207.tripeer.noti.dto.NotificationMap;
import com.j10d207.tripeer.noti.dto.Token;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NotificationMapper {

    private NotificationMapper() {

    }

    public static NotificationMap toNotificationMap(final List<Notification> notifications) {
        final Map<Long, NotificationDto> notificationMap = notifications.stream()
                .collect(Collectors.toMap(
                        Notification::getUserId,
                        NotificationMapper::toNotificationDto
                ));
        return new NotificationMap(notificationMap);
    }

    public static NotificationDto toNotificationDto(final Notification notification) {
        return NotificationDto.builder()
                .userId(notification.getUserId())
                .notificationId(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .msgType(notification.getMsgType().name())
                .startAt(notification.getStartAt())
                .build();
    }
}
