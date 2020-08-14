package com.fidecent.fbn.hx.repo;

import com.fidecent.fbn.hx.domain.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepo extends JpaRepository<Setting, Setting.Key> {
}
