package com.rahilhusain.hxevent.service.impl;

import com.rahilhusain.hxevent.domain.Event;
import com.rahilhusain.hxevent.domain.EventAttendee;
import com.rahilhusain.hxevent.dto.events.CreateEventRequest;
import com.rahilhusain.hxevent.dto.events.EventDetails;
import com.rahilhusain.hxevent.dto.events.EventDto;
import com.rahilhusain.hxevent.dto.events.SendRsvpInvites;
import com.rahilhusain.hxevent.dto.groups.DistributionGroupDto;
import com.rahilhusain.hxevent.mappers.DataMapper;
import com.rahilhusain.hxevent.repo.EventAttendeeRepo;
import com.rahilhusain.hxevent.repo.EventRepo;
import com.rahilhusain.hxevent.service.DistributionGroupService;
import com.rahilhusain.hxevent.service.EventService;
import com.rahilhusain.hxevent.service.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.List;
import java.util.Set;

@Service
public class EventServiceImpl implements EventService {
    private static final String DEV_PROFILE_NAME = "dev";
    private final EventRepo eventRepo;
    private final DataMapper dataMapper;
    private final DistributionGroupService groupService;
    private final MailService mailService;
    private final SpringTemplateEngine templateEngine;
    private final EventAttendeeRepo eventAttendeeRepo;
    private final Environment environment;

    @Value("${hx-events.app.mail.test-addr}")
    private String testMailAddr;


    public EventServiceImpl(EventRepo eventRepo, DataMapper dataMapper, DistributionGroupService groupService, MailService mailService, SpringTemplateEngine templateEngine, EventAttendeeRepo eventAttendeeRepo, Environment environment) {
        this.eventRepo = eventRepo;
        this.dataMapper = dataMapper;
        this.groupService = groupService;
        this.mailService = mailService;
        this.templateEngine = templateEngine;
        this.eventAttendeeRepo = eventAttendeeRepo;
        this.environment = environment;
    }

    @Override
    public Page<EventDto> findAll(Pageable pageable) {
        Page<Event> page = eventRepo.findAll(pageable);
        List<EventDto> content = dataMapper.mapEvents(page.getContent());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    public EventDetails create(CreateEventRequest request) {
        Event event = dataMapper.mapEventRequest(request);
        eventRepo.save(event);
        return dataMapper.mapEventDetails(event);
    }

    @Override
    public EventDetails getEventDetails(Long eventId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        return dataMapper.mapEventDetails(event);
    }

    @Override
    @Async
    public void sendRsvpInvites(ServletUriComponentsBuilder builder,
                                Long eventId, SendRsvpInvites request) {
        Set<DistributionGroupDto> groups = request.getGroups();
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        List<EventAttendee> attendees = groupService.getAttendeesForDistributionGroups(groups);
        String subject = "Invitation for event - " + event.getTitle();
        Context context = new Context();
        context.setVariable("event", event);
        if (isDevMode()) {
            attendees.add(new EventAttendee(testMailAddr, "TEST", "TEST DG"));
        }
        context.setVariable("decline", request.isDecline());
        for (EventAttendee attendee : attendees) {
            attendee.setEvent(event);
            eventAttendeeRepo.save(attendee);
            String url = builder
                    .pathSegment("events", eventId.toString(), "reply-rsvp", attendee.getId().toString())
                    .build().toUri().toString();
            context.setVariable("url", url);
            String content = templateEngine.process("rsvp-invitation", context);
            mailService.sendEmail(request.getName(), attendee.getEmail(), subject, content, request.getReplyTo());
        }

    }

    private boolean isDevMode() {
        String[] activeProfiles = this.environment.getActiveProfiles();
        return List.of(activeProfiles).contains(DEV_PROFILE_NAME);
    }
}
