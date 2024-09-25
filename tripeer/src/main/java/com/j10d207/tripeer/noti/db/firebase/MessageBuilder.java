package com.j10d207.tripeer.noti.db.firebase;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;

public class MessageBuilder {

	private MessageBuilder() {}

	private static final String TRIPEER_START_TITLE_TEMPLATE = "즐거운 여행 되세요!";
	private static final String TRIPEER_START_CONTENT_TEMPLATE = "계획하신 \"%s\" 여행 시작날입니다. %s님, 즐겁고 안전한 여행 되세요~!";

	private static final String DIARY_TITLE_TEMPLATE = "즐거운 여행 되셨나요?";
	private static final String DIARY_CONTENT_TEMPLATE = "%s님 다이어리를 통해 즐거운 추억을 간직해보세요";

	private static final String INVITE_TITLE_TEMPLATE = "%s님의 초대 메세지";
	private static final String INVITE_CONTENT_TEMPLATE = "\"%s\" 여행에 초대되셨습니다. 수락하시겠습니까?";

	public static MessageBody getMessageBody(final MessageType type, final String planTitle, final String nickname) {
		if (type.equals(MessageType.TRIPEER_START)) {
			return getTripeerStartBody(planTitle, nickname);
		}
		if (type.equals(MessageType.DIARY_SAVE)) {
			return getDiaryBody(nickname);
		}
		if (type.equals(MessageType.USER_INVITED)) {
			return getInviteBody(planTitle, nickname);
		}
		return null;
	}

	private static MessageBody getTripeerStartBody(final String planTitle, final String nickname) {
		final String title = String.format(TRIPEER_START_TITLE_TEMPLATE);
		final String content = String.format(TRIPEER_START_CONTENT_TEMPLATE, planTitle, nickname);
		return new MessageBody(title, content, MessageType.TRIPEER_START);
	}

	private static MessageBody getDiaryBody(final String nickname) {
		final String title = String.format(DIARY_TITLE_TEMPLATE);
		final String content = String.format(DIARY_CONTENT_TEMPLATE, nickname);
		return new MessageBody(title, content, MessageType.DIARY_SAVE);
	}

	private static MessageBody getInviteBody(final String planTitle, final String nickname) {
		final String title = String.format(INVITE_TITLE_TEMPLATE, nickname);
		final String content = String.format(INVITE_CONTENT_TEMPLATE, planTitle);
		return new MessageBody(title, content, MessageType.USER_INVITED);
	}

	public static Message toFirebaseMessage(final MessageBody messageBody, final String firebaseToken) {

		return Message.builder()
			.setToken(firebaseToken)
			.putAllData(messageBody.getBody())
			.setWebpushConfig(WebpushConfig.builder()
				.putHeader("ttl", "300")
				.build())
			.build();
	}
}
