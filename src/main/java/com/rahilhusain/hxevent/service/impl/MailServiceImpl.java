package com.rahilhusain.hxevent.service.impl;

import com.rahilhusain.hxevent.service.MailService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;

import static javax.mail.Message.RecipientType;

@Service
public class MailServiceImpl implements MailService {

    private static final String DEV_PROFILE_NAME = "dev";
    private final JavaMailSender mailSender;
    private final Environment environment;


    @Value("${hx-events.app.mail.reply-to}")
    private String replyToAddr;

    @Value("${spring.mail.username}")
    private String fromMailAddr;

    @Value("${hx-events.app.mail.from-name}")
    private String fromName;

    @Value("${hx-events.app.mail.test-addr}")
    private String testMailAddr;


    public MailServiceImpl(JavaMailSender mailSender, Environment environment) {
        this.mailSender = mailSender;
        this.environment = environment;
    }

    @SneakyThrows
    @Override
    public void sendEmail(String recipient, String subject, String content) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        mimeMessage.setSubject(subject);
        mimeMessage.setContent(content, "text/html");
        mimeMessage.setFrom(new InternetAddress(fromMailAddr, fromName));
        mimeMessage.setReplyTo(new Address[]{new InternetAddress(replyToAddr)});
        mimeMessage.addRecipient(RecipientType.TO, new InternetAddress(isDevMode() ? testMailAddr : recipient));
        mailSender.send(mimeMessage);
    }

    private boolean isDevMode() {
        String[] activeProfiles = this.environment.getActiveProfiles();
        return List.of(activeProfiles).contains(DEV_PROFILE_NAME);
    }
}
