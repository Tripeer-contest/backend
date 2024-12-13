package com.j10d207.tripeer.noti.dto;


import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record NotificationDto(Long notificationId, Long userId, String title, String content, String msgType, LocalDateTime startAt) {

}
