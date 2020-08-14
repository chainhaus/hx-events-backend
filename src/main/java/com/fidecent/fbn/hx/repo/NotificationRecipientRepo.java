package com.fidecent.fbn.hx.repo;

import com.fidecent.fbn.hx.domain.NotificationRecipient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRecipientRepo extends JpaRepository<NotificationRecipient, Integer> {
    boolean existsByEmail(String name);
}
