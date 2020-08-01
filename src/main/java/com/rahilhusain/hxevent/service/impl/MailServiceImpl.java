package com.rahilhusain.hxevent.service.impl;

import com.rahilhusain.hxevent.service.MailService;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static javax.mail.Message.RecipientType;

@Log4j2
@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;


    @Value("${hx-events.app.mail.reply-to}")
    private String replyToAddr;

    @Value("${hx-events.app.mail.from-addr}")
    private String fromMailAddr;

    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @SneakyThrows
    @Override
    public void sendEmail(String from, String recipient, String subject, String content) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        mimeMessage.setSubject(subject);
        mimeMessage.setContent(content, "text/html");
        mimeMessage.setFrom(new InternetAddress(fromMailAddr, from));
        mimeMessage.setReplyTo(new Address[]{new InternetAddress(replyToAddr)});
        mimeMessage.addRecipient(RecipientType.TO, new InternetAddress(recipient));
        log.info("Sending mail with subject \"{}\" to \"{}\"", subject, recipient);
        mailSender.send(mimeMessage);
    }

}
