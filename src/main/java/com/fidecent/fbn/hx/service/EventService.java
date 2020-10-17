package com.fidecent.fbn.hx.service;

import com.fidecent.fbn.hx.dto.events.CreateEventRequest;
import com.fidecent.fbn.hx.dto.events.EventDetails;
import com.fidecent.fbn.hx.dto.events.EventDto;
import com.fidecent.fbn.hx.dto.events.EventStatistic;
import com.fidecent.fbn.hx.dto.events.ReblastRequest;
import com.fidecent.fbn.hx.dto.events.SendRsvpInvites;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public interface EventService {
    Page<EventDto> findAll(Pageable pageable);

    EventDetails create(CreateEventRequest request);

    EventDetails getEventDetails(Long eventId);

    void sendRsvpInvites(Long eventId, SendRsvpInvites groupIds);

    void reblast(Long eventId, ReblastRequest request);
}
