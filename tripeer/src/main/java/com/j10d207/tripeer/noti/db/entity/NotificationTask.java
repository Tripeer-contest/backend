package com.j10d207.tripeer.noti.db.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "notification_task")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class NotificationTask {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "notification_id")
	private Notification notification;

	private String targetToken;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private NotificationTask.State state = State.WAIT;

	private enum State {
		WAIT, SENT;
	}

	public static NotificationTask of(final Notification notification, final String token) {
		return NotificationTask.builder()
			.notification(notification)
			.targetToken(token)
			.build();
	}

	public boolean isImmediately() {
		final LocalDate today =  LocalDate.now(ZoneId.of("Asia/Seoul"));
		final LocalDate startDate = notification.getStartAt().toLocalDate();
		return today.isAfter(startDate) || today.isEqual(startDate);
	}

	public boolean isScheduled() {
		final LocalDate today =  LocalDate.now(ZoneId.of("Asia/Seoul"));
		final LocalDate startDate = notification.getStartAt().toLocalDate();
		return today.isBefore(startDate);
	}

	public void toSENT() {
		this.state = State.SENT;
	}
}
