package com.rahilhusain.hxevent.repo;

import com.rahilhusain.hxevent.domain.ReplyTo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyToRepo extends JpaRepository<ReplyTo, Integer> {
    boolean existsByEmail(String name);
}
