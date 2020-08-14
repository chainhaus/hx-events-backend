package com.fidecent.fbn.hx.repo;

import com.fidecent.fbn.hx.domain.DisplayName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisplayNameRepo extends JpaRepository<DisplayName, Integer> {
    boolean existsByName(String name);
}
