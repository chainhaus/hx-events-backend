package com.fidecent.fbn.hx.service;

import com.fidecent.fbn.hx.domain.Mail;

public interface MailService {
    void sendEmail(String from, String recipient, String subject, String body, String replyTo);

    void queueMail(Mail mail);

    void sendQueuedMails();
}
