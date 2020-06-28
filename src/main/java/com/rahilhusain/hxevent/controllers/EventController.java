package com.rahilhusain.hxevent.controllers;

import com.rahilhusain.hxevent.dto.events.CreateEventRequest;
import com.rahilhusain.hxevent.dto.events.EventDetails;
import com.rahilhusain.hxevent.dto.events.EventDto;
import com.rahilhusain.hxevent.dto.events.SendRsvpInvites;
import com.rahilhusain.hxevent.service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
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
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentContextPath();
        eventService.sendRsvpInvites(builder, eventId, request.getGroups());
    }

    @PostMapping("{eventId}/reply-rsvp/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void replyRsvp(@PathVariable Long eventId, @PathVariable String token) {
        eventService.replyRsvp(eventId, token);
    }

}
