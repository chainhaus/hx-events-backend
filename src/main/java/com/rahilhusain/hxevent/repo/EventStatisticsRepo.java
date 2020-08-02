package com.rahilhusain.hxevent.repo;

import com.rahilhusain.hxevent.dto.events.EventStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventStatisticsRepo extends JpaRepository<EventStatistic, Long> {
}
