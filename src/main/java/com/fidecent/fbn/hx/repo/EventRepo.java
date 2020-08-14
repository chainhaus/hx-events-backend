package com.fidecent.fbn.hx.repo;

import com.fidecent.fbn.hx.domain.Event;
import com.fidecent.fbn.hx.dto.events.EventStatistic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepo extends JpaRepository<Event, Long> {
}
