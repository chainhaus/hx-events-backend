package com.rahilhusain.hxevent.repo;

import com.rahilhusain.hxevent.domain.Event;
import com.rahilhusain.hxevent.dto.events.EventStatistic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepo extends JpaRepository<Event, Long> {
}
