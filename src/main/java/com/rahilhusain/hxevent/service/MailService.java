package com.rahilhusain.hxevent.service;

public interface MailService {
    void sendEmail(String recipient, String subject, String body);
}
