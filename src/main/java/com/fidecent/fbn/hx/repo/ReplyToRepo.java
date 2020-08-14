package com.fidecent.fbn.hx.repo;

import com.fidecent.fbn.hx.domain.ReplyTo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyToRepo extends JpaRepository<ReplyTo, Integer> {
    boolean existsByEmail(String name);
}
