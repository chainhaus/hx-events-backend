package com.rahilhusain.hxevent.service;

public interface MailService {
    void sendEmail(String from, String recipient, String subject, String body, String replyTo);
}
