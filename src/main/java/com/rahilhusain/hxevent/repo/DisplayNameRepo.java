package com.rahilhusain.hxevent.repo;

import com.rahilhusain.hxevent.domain.DisplayName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisplayNameRepo extends JpaRepository<DisplayName, Integer> {
    boolean existsByName(String name);
}
