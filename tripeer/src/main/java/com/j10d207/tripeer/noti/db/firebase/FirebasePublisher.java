package com.j10d207.tripeer.noti.db.firebase;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.google.firebase.FirebaseException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;

/**
 * @author: 김회창
 *
 * 실제 Firbase 외부 자원을 사용하여 푸시알림을 전송하는 컴포넌트
 */
@Component
public class FirebasePublisher {

	public void sendFirebaseMessage(final Message message) throws FirebaseException {
		FirebaseMessaging instance = FirebaseMessaging.getInstance();
		instance.send(message);
	}
}
