package com.j10d207.tripeer.email.service;


import com.j10d207.tripeer.email.db.dto.EmailDTO;

public interface EmailService {

    public boolean sendEmail(EmailDTO emailDTO);
}
