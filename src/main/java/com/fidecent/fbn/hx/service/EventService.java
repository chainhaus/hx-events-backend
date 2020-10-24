package com.fidecent.fbn.hx.service;

import com.fidecent.fbn.hx.domain.Event;
import com.fidecent.fbn.hx.dto.events.CreateEventRequest;
import com.fidecent.fbn.hx.dto.events.EventDetails;
import com.fidecent.fbn.hx.dto.events.EventDto;
import com.fidecent.fbn.hx.dto.events.ReblastRequest;
import com.fidecent.fbn.hx.dto.events.SendRsvpInvites;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {
    Page<EventDto> findAll(Pageable pageable);

    EventDetails create(CreateEventRequest request);

    void sendEventCreationMail(Event event);

    EventDetails getEventDetails(Long eventId);

    void sendRsvpInvites(Long eventId, SendRsvpInvites groupIds);

    void reblast(Long eventId, ReblastRequest request);
}
