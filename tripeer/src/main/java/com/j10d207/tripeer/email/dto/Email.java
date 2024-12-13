package com.j10d207.tripeer.email.dto;

import java.util.Arrays;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Builder
@Slf4j
public class Email {

	private final String to;

	private final String subject;

	private final String from;

	private final String content;

	private static final String SUBJECT_PREFIX = "[Tripeer] ";

	private static final String SENDER_PREFIX = "보낸사람: ";

	public MimeMessagePreparator getEmailMessage() {

		return mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
			messageHelper.setTo(to);
			messageHelper.setSubject(makeSubject(subject));

			// HTML 컨텐츠 구성

			String htmlBody = buildEmailContent(this.content, from);
			if (from == null) htmlBody = buildEmailContent(this.content);
			messageHelper.setText(htmlBody, true); // true는 HTML 메일을 보내겠다는 의미.
		};
	}

	private String makeSubject(final String subject) {
		return SUBJECT_PREFIX + subject;
	}

	private String buildEmailContent(final String messageContent, final String sender) {
		final String htmlBody = makeEmailHtmlBody(messageContent, SENDER_PREFIX + sender);
		return makeHtml(htmlBody);
	}
	private String buildEmailContent(final String messageContent) {
		final String htmlBody = makeEmailHtmlBody(messageContent);
		return makeHtml(htmlBody);
	}

	private String makeEmailHtmlBody(final String ...contents) {
		String bodyHead = "<p style='font-size: 16px; line-height: 1.5; color: #333333;'>";
		String bodyEnd = "</p>";
		StringBuilder sb = new StringBuilder();
		sb.append(bodyHead);
		Arrays.stream(contents).forEach(content -> sb.append(bodyHead).append(content).append(bodyEnd));
		return sb.toString();
	}

	private String makeHtml(final String messageContent) {
		String body =
				"<html>" +
				"<body style='margin: 0; padding: 0; text-align: center; background-color: #f2f2f2;'>" +
				"<div style='padding-top: 40px;'>" +  // 이미지 위의 패딩만 유지
				"<img src='https://tripeer207.s3.ap-northeast-2.amazonaws.com/front/static/diaryBanner.png' alt='Tripper Welcome Image' style='width: 100%; height: auto; display: block; margin: 0 auto;'>" +  // 중앙 정렬
				"<div style='margin: 0 auto; background: white; border-top: 5px solid #4FBDB7; border-bottom: 5px solid #04ACB5; padding: 40px 20px; font-family: Arial, sans-serif; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05); min-height: 330px; text-align: center;'>" +  // 기본 높이와 패딩 조정, 텍스트 중앙 정렬 추가
				"<img src='https://tripeer207.s3.ap-northeast-2.amazonaws.com/front/static/title.png' alt='Tripper logo Image' style='max-width: 300px; width: 100%; height: auto; display: block; margin: 0 auto;'>" +
				"<h2 style='color: #04ACB5; margin-top: 50px;'>안녕하세요! Tripper입니다.</h2>";
		String end =
				"</div>" +
				"</body>" +
				"</html>";
		StringBuilder sb = new StringBuilder();

		sb.append(body).append(messageContent).append(end);

		return sb.toString();
	}
}
