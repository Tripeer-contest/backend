package com.j10d207.tripeer.noti.db.firebase;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.j10d207.tripeer.noti.db.firebase.MessageType;

public class MessageBody {

	private final Map<String, String> body;

	public MessageBody(final String title, final String content, final MessageType type) {
		body = Map.ofEntries(
			Map.entry("title", title),
			Map.entry("content", content),
			Map.entry("type", type.name())
		);
	}

	public Map<String, String> getBody() {
		return Map.copyOf(body);
	}

	public String getTitle() {
		return this.body.get("title");
	}

	public String getContent() {
		return this.body.get("content");
	}

	public MessageType getMessageType() {
		return MessageType.valueOf(this.body.get("type"));
	}
}
