package com.rahilhusain.hxevent.service;

import com.rahilhusain.hxevent.domain.Event;
import com.rahilhusain.hxevent.domain.EventAttendee;
import com.rahilhusain.hxevent.dto.ApproveRsvpRequest;
import com.rahilhusain.hxevent.dto.rsvp.RsvpDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RsvpService {
    Page<RsvpDto> findAll(Pageable pageable);

    Page<RsvpDto> findByEventId(Long eventId, Pageable pageable);

    void updateAttendeeStatus(Page<RsvpDto> page);

    void updateAttendeeStatus(RsvpDto.EventDto event);

    void replyInvitation(String invitationId, String reply);

    void approveInvitation(ApproveRsvpRequest request);

    void sendCalenderInvite(Event event, List<EventAttendee> attendee);

}
