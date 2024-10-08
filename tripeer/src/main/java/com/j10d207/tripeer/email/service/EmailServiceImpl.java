package com.j10d207.tripeer.email.service;


import com.j10d207.tripeer.email.db.EmailSender;
import com.j10d207.tripeer.email.dto.Email;
import com.j10d207.tripeer.email.dto.EmailDTO;
import com.j10d207.tripeer.email.dto.req.Helpdesk;
import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import com.j10d207.tripeer.user.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{

    private final UserRepository userRepository;
    private final EmailSender emailSender;

    private static final String HELP_DESK_RECEIVER = "tripeer207@gmail.com";
    @Override
    public boolean sendSimpleEmail(EmailDTO emailDTO) {
        UserEntity userEntity = userRepository.findById(emailDTO.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        String email = userEntity.getEmail();
        if (email == null || !email.contains("@")) { // 간단한 이메일 유효성 검사
//            throw new CustomException(ErrorCode.INVALID_EMAIL);
            return false;
        }

        final Email post = Email.builder()
            .subject(emailDTO.getTitle())
            .content(emailDTO.getContent())
            .to(email)
            .from(null)
            .build();

        try {
            emailSender.sendEmail(post);
            return true;
        } catch (Exception e) {
//            throw new CustomException(ErrorCode.EMAIL_NOT_FOUND);
            return false;
        }
    }

    @Override
    public void sendHelpdesk(final Helpdesk helpdesk) {

        final Email post = Email.builder()
            .subject(helpdesk.subject())
            .content(helpdesk.content())
            .to(HELP_DESK_RECEIVER)
            .from(helpdesk.sender())
            .build();

        try {
            emailSender.sendEmail(post);
        } catch (Exception e) {
            log.info("문제가 발생함: {}", e);
        }
    }

}
