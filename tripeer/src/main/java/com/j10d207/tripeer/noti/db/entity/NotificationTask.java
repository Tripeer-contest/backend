package com.j10d207.tripeer.noti.db.entity;

import java.time.LocalDateTime;

import com.j10d207.tripeer.noti.db.firebase.MessageType;
import com.j10d207.tripeer.noti.db.firebase.MessageBody;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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

	@OneToOne
	@JoinColumn(name = "token_id")
	private Notification token;

	private String title;

	private String content;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private State state = State.CREATED;

	@Enumerated(EnumType.STRING)
	private MessageType msgType;

	private LocalDateTime startAt;

	private enum State {

		CREATED,
		SCHEDULED,
		SENT
	}

	public static NotificationTask of(
		final MessageBody messageBody,
		final Notification notification,
		final LocalDateTime startAt
	) {
		return NotificationTask.builder()
				.token(notification)
				.title(messageBody.getTitle())
				.content(messageBody.getContent())
				.startAt(startAt)
				.msgType(messageBody.getMessageType())
				.build();
	}

	public void toSENT() {
		this.state = State.SENT;
	}
}
