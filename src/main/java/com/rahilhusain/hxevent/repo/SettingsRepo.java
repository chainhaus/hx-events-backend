package com.rahilhusain.hxevent.repo;

import com.rahilhusain.hxevent.domain.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepo extends JpaRepository<Setting, Setting.Key> {
}
