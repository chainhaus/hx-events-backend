package com.rahilhusain.hxevent.service.impl;

import com.rahilhusain.hxevent.domain.Mail;
import com.rahilhusain.hxevent.dto.MailSetting;
import com.rahilhusain.hxevent.repo.MailRepo;
import com.rahilhusain.hxevent.service.MailService;
import com.rahilhusain.hxevent.service.RsvpService;
import com.rahilhusain.hxevent.service.SettingsService;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static javax.mail.Message.RecipientType;

@Log4j2
@Service
public class MailServiceImpl implements MailService, ApplicationContextAware {

    private final JavaMailSender mailSender;
    private final MailRepo mailRepo;
    private final SettingsService settingsService;
    private RsvpService rsvpService;

    @Value("${hx-events.app.mail.from-addr}")
    private String fromMailAddr;

    public MailServiceImpl(JavaMailSender mailSender, MailRepo mailRepo, SettingsService settingsService) {
        this.mailSender = mailSender;
        this.mailRepo = mailRepo;
        this.settingsService = settingsService;
    }

    @SneakyThrows
    @Override
    public void sendEmail(String from, String recipient, String subject, String content, String replyTo) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        mimeMessage.setSubject(subject);
        mimeMessage.setContent(content, "text/html");
        mimeMessage.setFrom(new InternetAddress(fromMailAddr, from));
        if (replyTo != null) {
            mimeMessage.setReplyTo(new Address[]{new InternetAddress(replyTo)});
        }
        for (String to : recipient.split(";")) {
            mimeMessage.addRecipient(RecipientType.TO, new InternetAddress(to));
        }
        log.info("Sending mail with subject \"{}\" to \"{}\"", subject, recipient);
        mailSender.send(mimeMessage);
    }

    @Override
    public void queueMail(Mail mail) {
        mailRepo.save(mail);
    }

    @Override
    public void sendQueuedMails() {
        log.debug("Running Mail queue scheduler");
        MailSetting settings = settingsService.getMailQueueSettings();
        Integer batchSize = settings.getBatchSize();
        List<Mail> queuedMails = mailRepo.findAllByStatusInOrderByCreatedDate(Set.of(Mail.Status.QUEUED, Mail.Status.FAILED), PageRequest.of(0, batchSize));
        log.debug("{} queued mails found", queuedMails.size());
        for (Mail mail : queuedMails) {
            try {
                sendEmail(mail.getFromName(), mail.getToAddress(), mail.getSubject(), mail.getBody(), mail.getReplyToAddress());
                mail.setStatus(Mail.Status.SENT);
                if (mail.getType() == Mail.Type.APPROVED) {
                    rsvpService.sendCalenderInvite(mail.getAttendee().getEvent(), Collections.singletonList(mail.getAttendee()));
                }
            } catch (Exception e) {
                log.catching(e);
                mail.setStatus(Mail.Status.FAILED);
            }
            mailRepo.save(mail);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.rsvpService = applicationContext.getBean(RsvpService.class);
    }
}
