package com.fidecent.fbn.hx.service;

import com.fidecent.fbn.hx.domain.Event;
import com.fidecent.fbn.hx.domain.EventAttendee;
import com.fidecent.fbn.hx.dto.UpdateRsvpRequest;
import com.fidecent.fbn.hx.dto.rsvp.RsvpDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RsvpService {
    Page<RsvpDto> findAll(String search, Pageable pageable);

    Page<RsvpDto> findByEventId(Long eventId, String search, Pageable pageable);

    void updateAttendeeStatus(Page<RsvpDto> page);

    void updateAttendeeStatus(RsvpDto.EventDto event);

    void replyInvitation(String invitationId, String reply);

    void updateRsvpStatus(UpdateRsvpRequest request);

    void sendCalenderInvite(Event event, List<EventAttendee> attendee);

    void markOpened(String invitationToken);
}
