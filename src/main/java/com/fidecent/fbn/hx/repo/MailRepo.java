package com.fidecent.fbn.hx.repo;

import com.fidecent.fbn.hx.domain.Mail;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface MailRepo extends JpaRepository<Mail, Long> {
    List<Mail> findAllByStatusInOrderByCreatedDate(Collection<Mail.Status> status, Pageable pageable);
}
