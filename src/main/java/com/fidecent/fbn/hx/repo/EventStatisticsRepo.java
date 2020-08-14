package com.fidecent.fbn.hx.repo;

import com.fidecent.fbn.hx.dto.events.EventStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventStatisticsRepo extends JpaRepository<EventStatistic, Long> {
}
