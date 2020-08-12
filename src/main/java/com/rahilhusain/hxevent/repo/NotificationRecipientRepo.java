package com.rahilhusain.hxevent.repo;

import com.rahilhusain.hxevent.domain.NotificationRecipient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRecipientRepo extends JpaRepository<NotificationRecipient, Integer> {
    boolean existsByEmail(String name);
}
