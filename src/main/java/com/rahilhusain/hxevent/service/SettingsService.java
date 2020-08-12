package com.rahilhusain.hxevent.service;

import com.rahilhusain.hxevent.domain.DisplayName;
import com.rahilhusain.hxevent.domain.NotificationRecipient;
import com.rahilhusain.hxevent.domain.ReplyTo;
import com.rahilhusain.hxevent.dto.MailSetting;

import java.util.List;

public interface SettingsService {
    List<DisplayName> getMailDisplayNames();

    void addDisplayName(DisplayName request);

    void deleteDisplayName(Integer id);

    List<ReplyTo> getReplyToList();

    void addReplyTo(ReplyTo request);

    void deleteReplyTo(Integer id);

    MailSetting getMailQueueSettings();

    void updateMailQueueSettings(MailSetting mailSetting);

    List<NotificationRecipient> getNotificationRecipients();

    void addNotificationRecipient(NotificationRecipient request);

    void deleteNotificationRecipient(Integer id);
}
