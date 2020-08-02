package com.rahilhusain.hxevent.controllers;

import com.rahilhusain.hxevent.dto.events.CreateEventRequest;
import com.rahilhusain.hxevent.dto.events.EventDetails;
import com.rahilhusain.hxevent.dto.events.EventDto;
import com.rahilhusain.hxevent.dto.events.EventStatistic;
import com.rahilhusain.hxevent.dto.events.SendRsvpInvites;
import com.rahilhusain.hxevent.repo.EventStatisticsRepo;
import com.rahilhusain.hxevent.service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("api/events")
public class EventController {

    private final EventService eventService;
    private final EventStatisticsRepo eventStatisticsRepo;

    public EventController(EventService eventService, EventStatisticsRepo eventStatisticsRepo) {
        this.eventService = eventService;
        this.eventStatisticsRepo = eventStatisticsRepo;
    }

    @GetMapping
    public Page<EventDto> getAllEvents(Pageable pageable) {
        return eventService.findAll(pageable);
    }

    @GetMapping("{eventId}")
    public EventDetails getEvent(@PathVariable Long eventId) {
        return eventService.getEventDetails(eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDetails createEvent(@Valid @RequestBody CreateEventRequest request) {
        return eventService.create(request);
    }

    @PostMapping("{eventId}/send-invites")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sendRsvpInvites(@PathVariable Long eventId, @RequestBody @Valid SendRsvpInvites request) {
        eventService.sendRsvpInvites(eventId, request);
    }

    @GetMapping("statistics")
    public Page<EventStatistic> getEventStatistics(Pageable pageable) {
        return eventStatisticsRepo.findAll(pageable);
    }

}
