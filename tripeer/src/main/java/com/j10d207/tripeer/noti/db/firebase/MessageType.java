package com.j10d207.tripeer.noti.db.firebase;

import java.util.Map;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MessageType {

	DIARY_SAVE("DIARY_TITLE", "DIARY_CONTENT"),

	USER_INVITED("USER_INVITED_TITLE", "USER_INVITED_CONTENT"),

	TRIPEER_START("TRIPEER_START_TITLE", "TRIPEER_START_CONTENT")
	;

	private final String title;
	private final String content;

	private Map<String, String> makeBody() {
		return Map.ofEntries(
			Map.entry("title", this.title),
			Map.entry("content", this.content),
			Map.entry("type", this.name())
		);
	}

	public Message build(final String firebaseToken) {
		return Message.builder()
			.setToken(firebaseToken)
			.putAllData(makeBody())
			.setWebpushConfig(WebpushConfig.builder()
				.putHeader("ttl", "300")
				.build())
			.build();
	}

}
