package com.j10d207.tripeer.noti.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class NotificationExceptionHandler {

	@ExceptionHandler(FirebaseInvalidTokenException.class)
	public void FirebaseInvalidTokenExceptionHandler() {

	}
}
