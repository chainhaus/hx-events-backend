package com.rahilhusain.hxevent.service;

import com.rahilhusain.hxevent.domain.DisplayName;

import java.util.List;

public interface SettingsService {
    List<DisplayName> getMailDisplayNames();

    void addDisplayName(DisplayName request);

    void deleteDisplayName(Integer id);
}
