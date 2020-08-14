package com.fidecent.fbn.hx.service.impl;

import com.fidecent.fbn.hx.domain.DisplayName;
import com.fidecent.fbn.hx.domain.NotificationRecipient;
import com.fidecent.fbn.hx.domain.ReplyTo;
import com.fidecent.fbn.hx.domain.Setting;
import com.fidecent.fbn.hx.dto.MailSetting;
import com.fidecent.fbn.hx.repo.DisplayNameRepo;
import com.fidecent.fbn.hx.repo.NotificationRecipientRepo;
import com.fidecent.fbn.hx.repo.ReplyToRepo;
import com.fidecent.fbn.hx.repo.SettingsRepo;
import com.fidecent.fbn.hx.service.SettingsService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SettingsServiceImpl implements SettingsService {
    private final DisplayNameRepo displayNameRepo;
    private final ReplyToRepo replyToRepo;
    private final SettingsRepo settingsRepo;
    private final NotificationRecipientRepo notificationRecipientRepo;

    public SettingsServiceImpl(DisplayNameRepo displayNameRepo, ReplyToRepo replyToRepo, SettingsRepo settingsRepo, NotificationRecipientRepo notificationRecipientRepo) {
        this.displayNameRepo = displayNameRepo;
        this.replyToRepo = replyToRepo;
        this.settingsRepo = settingsRepo;
        this.notificationRecipientRepo = notificationRecipientRepo;
    }

    @Override
    public List<DisplayName> getMailDisplayNames() {
        return displayNameRepo.findAll();
    }

    @Override
    public void addDisplayName(DisplayName request) {
        if (displayNameRepo.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Display Name already exists");
        }
        displayNameRepo.save(request);
    }

    @Override
    public void deleteDisplayName(Integer id) {
        displayNameRepo.deleteById(id);
    }

    @Override
    public List<ReplyTo> getReplyToList() {
        return replyToRepo.findAll();
    }

    @Override
    public void addReplyTo(ReplyTo request) {
        if (replyToRepo.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Reply To already exists");
        }
        replyToRepo.save(request);
    }

    @Override
    public void deleteReplyTo(Integer id) {
        replyToRepo.deleteById(id);
    }

    @Override
    public MailSetting getMailQueueSettings() {
        List<Setting> settings = settingsRepo.findAllById(List.of(Setting.Key.QUEUE_INTERVAL, Setting.Key.QUEUE_BATCH_SIZE));
        Integer interval = settings.stream().filter(s -> s.getKey() == Setting.Key.QUEUE_INTERVAL).map(Setting::getValue).findAny().map(Integer::valueOf).orElse(5);
        Integer batchSize = settings.stream().filter(s -> s.getKey() == Setting.Key.QUEUE_BATCH_SIZE).map(Setting::getValue).findAny().map(Integer::valueOf).orElse(100);
        return new MailSetting(batchSize, interval);
    }

    @Override
    public void updateMailQueueSettings(MailSetting mailSetting) {
        settingsRepo.saveAll(List.of(
                new Setting(Setting.Key.QUEUE_INTERVAL, mailSetting.getInterval().toString()),
                new Setting(Setting.Key.QUEUE_BATCH_SIZE, mailSetting.getBatchSize().toString())
        ));
    }

    @Override
    public List<NotificationRecipient> getNotificationRecipients() {
        return notificationRecipientRepo.findAll();
    }

    @Override
    public void addNotificationRecipient(NotificationRecipient request) {
        if (notificationRecipientRepo.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Notification Recipient already exists");
        }
        notificationRecipientRepo.save(request);
    }

    @Override
    public void deleteNotificationRecipient(Integer id) {
        notificationRecipientRepo.deleteById(id);
    }
}
