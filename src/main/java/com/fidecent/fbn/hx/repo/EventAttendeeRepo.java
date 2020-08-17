package com.fidecent.fbn.hx.repo;

import com.fidecent.fbn.hx.domain.EventAttendee;
import com.fidecent.fbn.hx.dto.rsvp.RsvpDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventAttendeeRepo extends JpaRepository<EventAttendee, Long> {
    Page<RsvpDto> findAllProjectedBy(Pageable pageable);

    Page<RsvpDto> findAllByEventId(Long eventId, Pageable pageable);

    Optional<EventAttendee> findOneByToken(String token);

    @Query("SELECT a.email FROM EventAttendee a JOIN a.event e WHERE e.externalId = :externalId AND a.calenderSent = true")
    Set<String> findAllInvitedAttendeesForCalenderEvent(@Param("externalId") String externalId);

    List<EventAttendee> findAllByEventIdAndCalenderSentTrue(Long id);
}
