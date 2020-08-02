package com.rahilhusain.hxevent.repo;

import com.rahilhusain.hxevent.domain.EventAttendee;
import com.rahilhusain.hxevent.dto.rsvp.RsvpDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface EventAttendeeRepo extends JpaRepository<EventAttendee, UUID> {
    Page<RsvpDto> findAllProjectedBy(Pageable pageable);

    Page<RsvpDto> findAllByEventId(Long eventId, Pageable pageable);

    @SuppressWarnings("JpaQlInspection")
    @Query("SELECT a.email FROM EventAttendee a JOIN a.event e WHERE e.externalId = :externalId AND a.status NOT IN ('RSVP_SENT', 'RSVP_ACCEPTED', 'RSVP_DECLINED')")
    Set<String> findAllApprovedAttendeesForCalenderEvent(String externalId);

    List<EventAttendee> findAllByEventIdAndStatusIn(Long id, EventAttendee.Status... calenderSent);
}
