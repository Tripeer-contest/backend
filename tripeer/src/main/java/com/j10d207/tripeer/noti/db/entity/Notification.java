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

@Entity(name = "notification")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private State state = State.RECEIVE;

	private String title;

	private String content;

	@Enumerated(EnumType.STRING)
	private MessageType msgType;

	private Long targetId;

	private LocalDateTime startAt;

	public enum State {

		RECEIVE,
		READ
	}

	public static Notification of(
		final MessageBody messageBody,
		final Long userId,
		final LocalDateTime startAt,
		final Long targetId
	) {
		return Notification.builder()
				.title(messageBody.getTitle())
				.content(messageBody.getContent())
				.targetId(targetId)
				.startAt(startAt)
				.userId(userId)
				.msgType(messageBody.getMessageType())
				.build();
	}

	public static Notification ofState(
		final MessageBody messageBody,
		final FirebaseToken firebaseToken,
		final LocalDateTime startAt,
		final Long targetId,
		final Notification.State state
	) {
		return Notification.builder()
			.title(messageBody.getTitle())
			.content(messageBody.getContent())
			.startAt(startAt)
			.state(state)
			.msgType(messageBody.getMessageType())
			.userId(firebaseToken.getUser().getUserId())
			.targetId(targetId)
			.build();
	}



	public void toRECEIVE() {
		this.state = State.RECEIVE;
	}

	public void toREAD() {
		this.state = State.READ;
	}
}
