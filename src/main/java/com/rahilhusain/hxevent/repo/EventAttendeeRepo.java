package com.rahilhusain.hxevent.repo;

import com.rahilhusain.hxevent.domain.EventAttendee;
import com.rahilhusain.hxevent.dto.rsvp.RsvpDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface EventAttendeeRepo extends JpaRepository<EventAttendee, Long> {
    Page<RsvpDto> findAllProjectedBy(Pageable pageable);

    Page<RsvpDto> findAllByEventId(Long eventId, Pageable pageable);

    Optional<EventAttendee> findOneByToken(UUID uuid);

    @Query("SELECT a.email FROM EventAttendee a JOIN a.event e WHERE e.externalId = :externalId AND a.calenderSent = true")
    Set<String> findAllInvitedAttendeesForCalenderEvent(@Param("externalId") String externalId);

    List<EventAttendee> findAllByEventIdAndCalenderSentTrue(Long id);
}
