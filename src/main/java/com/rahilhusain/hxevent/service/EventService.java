package com.rahilhusain.hxevent.service;

import com.rahilhusain.hxevent.dto.events.CreateEventRequest;
import com.rahilhusain.hxevent.dto.events.EventDetails;
import com.rahilhusain.hxevent.dto.events.EventDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface EventService {
    Page<EventDto> findAll(Pageable pageable);

    EventDetails create(CreateEventRequest request);

    EventDetails getEventDetails(Long eventId);

    void sendRsvpInvites(Long eventId, Set<String> groupIds);

    void replyRsvp(Long eventId, String token);
}
