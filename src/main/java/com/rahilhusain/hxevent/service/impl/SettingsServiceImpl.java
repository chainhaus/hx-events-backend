package com.rahilhusain.hxevent.service.impl;

import com.rahilhusain.hxevent.domain.DisplayName;
import com.rahilhusain.hxevent.domain.ReplyTo;
import com.rahilhusain.hxevent.domain.Setting;
import com.rahilhusain.hxevent.dto.MailSetting;
import com.rahilhusain.hxevent.repo.DisplayNameRepo;
import com.rahilhusain.hxevent.repo.ReplyToRepo;
import com.rahilhusain.hxevent.repo.SettingsRepo;
import com.rahilhusain.hxevent.service.SettingsService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SettingsServiceImpl implements SettingsService {
    private final DisplayNameRepo displayNameRepo;
    private final ReplyToRepo replyToRepo;
    private final SettingsRepo settingsRepo;

    public SettingsServiceImpl(DisplayNameRepo displayNameRepo, ReplyToRepo replyToRepo, SettingsRepo settingsRepo) {
        this.displayNameRepo = displayNameRepo;
        this.replyToRepo = replyToRepo;
        this.settingsRepo = settingsRepo;
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
}
