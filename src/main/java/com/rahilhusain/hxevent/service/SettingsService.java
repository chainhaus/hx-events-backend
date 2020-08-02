package com.rahilhusain.hxevent.service;

import com.rahilhusain.hxevent.domain.DisplayName;
import com.rahilhusain.hxevent.domain.ReplyTo;

import java.util.List;

public interface SettingsService {
    List<DisplayName> getMailDisplayNames();

    void addDisplayName(DisplayName request);

    void deleteDisplayName(Integer id);

    List<ReplyTo> getReplyToList();

    void addReplyTo(ReplyTo request);

    void deleteReplyTo(Integer id);
}
