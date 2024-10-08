package com.j10d207.tripeer.email.service;


import com.j10d207.tripeer.email.dto.EmailDTO;
import com.j10d207.tripeer.email.dto.req.Helpdesk;

public interface EmailService {

    public boolean sendSimpleEmail(EmailDTO emailDTO);

    void sendHelpdesk(final Helpdesk helpdesk);

}
