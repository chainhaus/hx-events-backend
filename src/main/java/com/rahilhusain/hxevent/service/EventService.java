package com.rahilhusain.hxevent.service;

import com.rahilhusain.hxevent.dto.events.CreateEventRequest;
import com.rahilhusain.hxevent.dto.events.EventDetails;
import com.rahilhusain.hxevent.dto.events.EventDto;
import com.rahilhusain.hxevent.dto.events.EventStatistic;
import com.rahilhusain.hxevent.dto.events.SendRsvpInvites;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public interface EventService {
    Page<EventDto> findAll(Pageable pageable);

    EventDetails create(CreateEventRequest request);

    EventDetails getEventDetails(Long eventId);

    void sendRsvpInvites(Long eventId, SendRsvpInvites groupIds);
}
