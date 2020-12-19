package com.fidecent.fbn.hx.service.impl;

import com.fidecent.fbn.hx.domain.Mail;
import com.fidecent.fbn.hx.dto.MailSetting;
import com.fidecent.fbn.hx.repo.MailRepo;
import com.fidecent.fbn.hx.service.MailService;
import com.fidecent.fbn.hx.service.RsvpService;
import com.fidecent.fbn.hx.service.SettingsService;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
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
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final MailRepo mailRepo;
    private final SettingsService settingsService;

    @Lazy
    @Autowired
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
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
    @Async
    public void sendEmail(String from, String recipient, String subject, String content, String replyTo) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        mimeMessage.setSubject(subject);
        mimeMessage.setContent(content, "text/html; charset=UTF-8");
        mimeMessage.setFrom(new InternetAddress(fromMailAddr, from));
        if (replyTo != null) {
            mimeMessage.setReplyTo(new Address[]{new InternetAddress(replyTo)});
        }
        for (String to : recipient.split(";")) {
            if (StringUtils.isNotBlank(to)) {
                mimeMessage.addRecipient(RecipientType.TO, new InternetAddress(to));
            }
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
                log.info("{} was sent {} mail for the event: {}-{}", mail.getToAddress(), mail.getType(), mail.getAttendee().getEvent().getId(), mail.getAttendee().getEvent().getTitle());
            } catch (Exception e) {
                log.catching(e);
                mail.setStatus(Mail.Status.FAILED);
            }
            mailRepo.save(mail);
        }
    }

    @Async
    @Override
    public void sendAsync(Mail mail) {
        sendEmail(mail.getFromName(), mail.getToAddress(), mail.getSubject(), mail.getBody(), mail.getReplyToAddress());
    }
}
