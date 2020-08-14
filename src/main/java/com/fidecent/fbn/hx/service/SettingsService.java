package com.fidecent.fbn.hx.service;

import com.fidecent.fbn.hx.domain.DisplayName;
import com.fidecent.fbn.hx.domain.NotificationRecipient;
import com.fidecent.fbn.hx.domain.ReplyTo;
import com.fidecent.fbn.hx.dto.MailSetting;

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
