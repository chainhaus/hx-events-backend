package com.fidecent.fbn.hx.service.impl;

import com.fidecent.fbn.hx.HashUtils;
import com.fidecent.fbn.hx.domain.Event;
import com.fidecent.fbn.hx.domain.EventAttendee;
import com.fidecent.fbn.hx.domain.Mail;
import com.fidecent.fbn.hx.dto.events.CreateEventRequest;
import com.fidecent.fbn.hx.dto.events.EventDetails;
import com.fidecent.fbn.hx.dto.events.EventDto;
import com.fidecent.fbn.hx.dto.events.SendRsvpInvites;
import com.fidecent.fbn.hx.dto.groups.DistributionGroupDto;
import com.fidecent.fbn.hx.mappers.DataMapper;
import com.fidecent.fbn.hx.repo.EventAttendeeRepo;
import com.fidecent.fbn.hx.repo.EventRepo;
import com.fidecent.fbn.hx.service.DistributionGroupService;
import com.fidecent.fbn.hx.service.EventService;
import com.fidecent.fbn.hx.service.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.*;

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
    public void sendRsvpInvites(Long eventId, SendRsvpInvites request) {
        Set<DistributionGroupDto> groups = request.getGroups();
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        List<EventAttendee> attendees = groupService.getAttendeesForDistributionGroups(groups);
        String subject = "Invitation for event - " + event.getTitle();
        Context context = new Context();
        context.setVariable("event", event);
        if (isDevMode()) {
            attendees.add(new EventAttendee(testMailAddr, "TEST", "TEST DG", "Rahil", "Husain"));
        }
        context.setVariable("decline", request.isDecline());
        for (EventAttendee attendee : attendees) {
            attendee.setToken(HashUtils.generateHash(attendee.getEmail(), event.getDate(), event.getStartTime(), event.getEndTime(), event.getTitle()));
            attendee.setEvent(event);
            eventAttendeeRepo.save(attendee);
            String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .pathSegment("events", eventId.toString(), "reply-rsvp", attendee.getToken())
                    .build().toUri().toString();
            context.setVariable("url", url);
            String trackUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .pathSegment("api", "rsvp", attendee.getToken(), "copyright.png")
                    .build().toUri().toString();
            context.setVariable("trackUrl", trackUrl);
            String content = templateEngine.process("rsvp-invitation", context);
            Mail mail = new Mail();
            mail.setSubject(subject);
            mail.setBody(content);
            mail.setAttendee(attendee);
            mail.setFromName(request.getName());
            mail.setToAddress(attendee.getEmail());
            mail.setReplyToAddress(request.getReplyTo());
            mail.setType(Mail.Type.RSVP);
            mailService.queueMail(mail);
        }

    }

    private boolean isDevMode() {
        String[] activeProfiles = this.environment.getActiveProfiles();
        return List.of(activeProfiles).contains(DEV_PROFILE_NAME);
    }
}
