package com.j10d207.tripeer.email.db;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import com.j10d207.tripeer.email.dto.Email;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmailSender {

	private final JavaMailSender sender;

	@Async
	public void sendEmail(final Email email) throws MailException {

		sender.send(email.getEmailMessage());
	}
}
