package com.j10d207.tripeer.noti.db.firebase;

import org.springframework.stereotype.Component;

import com.google.firebase.FirebaseException;
import com.google.firebase.messaging.FirebaseMessaging;


@Component

/**
 * @author: 김회창
 *
 * 실제 Firbase 외부 자원을 사용하여 푸시알림을 전송하는 컴포넌트
 */
public class FirebasePublisher {

	private final FirebaseMessaging messageHelper;

	public FirebasePublisher() {
		messageHelper = FirebaseMessaging.getInstance();
	}

	public void sendFirebaseMessage(final MessageType messageType) throws FirebaseException {

	}
}
