package com.rahilhusain.hxevent.repo;

import com.rahilhusain.hxevent.domain.EventAttendee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventAttendeeRepo extends JpaRepository<EventAttendee, UUID> {
}
