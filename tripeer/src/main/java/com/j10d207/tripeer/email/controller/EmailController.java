package com.j10d207.tripeer.email.controller;


import com.j10d207.tripeer.email.dto.EmailDTO;
import com.j10d207.tripeer.email.dto.req.Helpdesk;
import com.j10d207.tripeer.email.service.EmailService;
import com.j10d207.tripeer.response.Response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("")
    public Response<?> test(@RequestBody EmailDTO emailDTO) {
        return Response.of(HttpStatus.OK, "이메일 전송 완료", emailService.sendSimpleEmail(emailDTO));
    }

    @PostMapping("/helpdesk")
    public Response<?> helpdeskProcessor(@RequestBody Helpdesk helpdesk) {

        emailService.sendHelpdesk(helpdesk);
        return Response.of(
            ResponseHeader.EMAIL_SENT.getStatus(),
            ResponseHeader.EMAIL_SENT.getMessage(),
            null
        );
    }

    @Getter
    @RequiredArgsConstructor
    private enum ResponseHeader {

        EMAIL_SENT("이메일 전송에 성공하였습니다.", HttpStatus.NO_CONTENT)
        ;

        private final String message;
        private final HttpStatus status;

    }
}
