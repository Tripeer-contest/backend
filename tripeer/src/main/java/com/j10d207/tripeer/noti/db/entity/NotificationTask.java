package com.j10d207.tripeer.noti.db.entity;

import java.time.LocalDate;
import java.time.ZoneId;

import com.j10d207.tripeer.noti.db.firebase.MessageType;
import com.j10d207.tripeer.noti.dto.NotificationDto;
import com.j10d207.tripeer.noti.dto.Token;
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

	@ManyToOne
	@JoinColumn(name = "target_token")
	private FirebaseToken targetToken;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private NotificationTask.State state = State.WAIT;

	private enum State {
		WAIT, SENT;
	}

	public static NotificationTask of(final NotificationDto notification, final Token token) {
		return NotificationTask.builder()
			.notification(Notification.builder()
					.id(notification.notificationId())
					.userId(notification.userId())
					.title(notification.title())
					.msgType(MessageType.valueOf(notification.msgType()))
					.content(notification.content())
					.startAt(notification.startAt())
					.build())
			.targetToken(FirebaseToken.from(token.tokenId(), token.firebaseToken(), token.type()))
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
