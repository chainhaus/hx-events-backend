package com.rahilhusain.hxevent.service.impl;

import com.rahilhusain.hxevent.service.MailService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static javax.mail.Message.RecipientType;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;


    @Value("${hx-events.app.mail.reply-to}")
    private String replyToAddr;

    @Value("${spring.mail.username}")
    private String fromMailAddr;

    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @SneakyThrows
    @Override
    public void sendEmail(String recipient, String subject, String content) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        mimeMessage.setSubject(subject);
        mimeMessage.setContent(content, "text/html");
        mimeMessage.setFrom(fromMailAddr);
        mimeMessage.setReplyTo(new Address[]{new InternetAddress(replyToAddr)});
        mimeMessage.addRecipient(RecipientType.TO, new InternetAddress(recipient));
        mailSender.send(mimeMessage);
    }
}
