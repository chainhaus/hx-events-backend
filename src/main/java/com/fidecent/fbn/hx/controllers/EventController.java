package com.fidecent.fbn.hx.controllers;

import com.fidecent.fbn.hx.dto.events.CreateEventRequest;
import com.fidecent.fbn.hx.dto.events.EventDetails;
import com.fidecent.fbn.hx.dto.events.EventDto;
import com.fidecent.fbn.hx.dto.events.EventStatistic;
import com.fidecent.fbn.hx.dto.events.ReblastRequest;
import com.fidecent.fbn.hx.dto.events.SendRsvpInvites;
import com.fidecent.fbn.hx.repo.EventStatisticsRepo;
import com.fidecent.fbn.hx.service.EventService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

@Log4j2
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
    public Page<EventDto> getAllEvents(@PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
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

    @PostMapping("{eventId}/reblast")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reblast(@PathVariable Long eventId, @RequestBody @Valid ReblastRequest request) {
        eventService.reblast(eventId, request);
    }

    @GetMapping("statistics")
    public Page<EventStatistic> getEventStatistics(Pageable pageable) {
        return eventStatisticsRepo.findAll(pageable);
    }

}
