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
    @Query("SELECT e FROM EventAttendee e WHERE e.email LIKE :search OR e.firstName LIKE :search OR e.lastName LIKE :search")
    Page<RsvpDto> findAllProjectedBy(@Param("search") String search, Pageable pageable);

    Page<RsvpDto> findAllProjectedBy(Pageable pageable);

    Page<RsvpDto> findAllByEventId(Long eventId, Pageable pageable);

    @Query("SELECT e FROM EventAttendee e WHERE e.event.id = :eventId AND (e.email LIKE :search OR e.firstName LIKE :search OR e.lastName LIKE :search)")
    Page<RsvpDto> findAllByEventId(@Param("eventId") Long eventId, @Param("search") String search, Pageable pageable);

    Optional<EventAttendee> findOneByToken(String token);

    @Query("SELECT a.email FROM EventAttendee a JOIN a.event e WHERE e.externalId = :externalId AND a.approvalMailSent = true")
    Set<String> findAllInvitedAttendeesForCalenderEvent(@Param("externalId") String externalId);

    List<EventAttendee> findAllByEventIdAndCalenderSentTrue(Long id);

    List<EventAttendee> findAllByEventIdAndRsvpAcceptedFalseAndRsvpDeclinedFalse(Long eventId);
}
