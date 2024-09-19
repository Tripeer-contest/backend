package com.j10d207.tripeer.noti.db.firebase;

import java.util.Map;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MessageType {

	DIARY_SAVE,

	USER_INVITED,

	TRIPEER_START
	;

}
