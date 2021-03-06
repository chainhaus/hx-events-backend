package com.fidecent.fbn.hx.service.impl;

import com.fidecent.fbn.hx.HashUtils;
import com.fidecent.fbn.hx.domain.Event;
import com.fidecent.fbn.hx.domain.EventAttendee;
import com.fidecent.fbn.hx.domain.Mail;
import com.fidecent.fbn.hx.dto.events.CreateEventRequest;
import com.fidecent.fbn.hx.dto.events.EventDetails;
import com.fidecent.fbn.hx.dto.events.EventDto;
import com.fidecent.fbn.hx.dto.events.ReblastRequest;
import com.fidecent.fbn.hx.dto.events.SendRsvpInvites;
import com.fidecent.fbn.hx.dto.groups.DistributionGroupDto;
import com.fidecent.fbn.hx.dto.rsvp.RsvpDto;
import com.fidecent.fbn.hx.mappers.DataMapper;
import com.fidecent.fbn.hx.repo.EventAttendeeRepo;
import com.fidecent.fbn.hx.repo.EventRepo;
import com.fidecent.fbn.hx.repo.MailRepo;
import com.fidecent.fbn.hx.service.DistributionGroupService;
import com.fidecent.fbn.hx.service.EventService;
import com.fidecent.fbn.hx.service.MailService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
public class EventServiceImpl implements EventService {
    private static final String DEV_PROFILE_NAME = "dev";
    private final EventRepo eventRepo;
    private final DataMapper dataMapper;
    private final DistributionGroupService groupService;
    private final MailService mailService;
    private final SpringTemplateEngine templateEngine;
    private final EventAttendeeRepo eventAttendeeRepo;
    private final MailRepo mailRepo;
    private final Environment environment;

    @Value("${hx-events.app.mail.test-addr}")
    private String testMailAddr;

    @Value("${hx-events.app.mail.new-event-notification}")
    private String newEventNotificationRecipient;


    public EventServiceImpl(EventRepo eventRepo, DataMapper dataMapper, DistributionGroupService groupService, MailService mailService, SpringTemplateEngine templateEngine, EventAttendeeRepo eventAttendeeRepo, MailRepo mailRepo, Environment environment) {
        this.eventRepo = eventRepo;
        this.dataMapper = dataMapper;
        this.groupService = groupService;
        this.mailService = mailService;
        this.templateEngine = templateEngine;
        this.eventAttendeeRepo = eventAttendeeRepo;
        this.mailRepo = mailRepo;
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
        log.info("New Event created: {}", request);
        sendEventCreationMail(event);
        return dataMapper.mapEventDetails(event);
    }

    @Override
    public void sendEventCreationMail(Event event) {
        Context context = new Context();
        context.setVariable("event", event);
        String content = templateEngine.process("event", context);
        mailService.sendEmail("HX Events", newEventNotificationRecipient, "New Event Created", content, null);
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
        String subject = event.getTitle();
        Context context = new Context();
        context.setVariable("event", event);
        if (isDevMode()) {
            attendees.clear();
            attendees.add(new EventAttendee(testMailAddr, "TEST", "TEST DG", "Rahil", "Husain"));
        }
        context.setVariable("decline", request.isDecline());
        Set<String> existingAttendees = eventAttendeeRepo.findAllByEventId(eventId, Pageable.unpaged())
                .stream()
                .map(RsvpDto::getEmail)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        for (EventAttendee attendee : attendees) {
            attendee.setAllowDecline(request.isDecline());
            if (existingAttendees.contains(attendee.getEmail().toLowerCase())) {
                //TODO: remove this later. Skip if already sent
                continue;
            }
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

    @Override
    @Transactional
    public void reblast(Long eventId, ReblastRequest request) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        event.setDescription(request.getDescription());
        event.setEmailSubject(request.getSubject());
        List<EventAttendee> attendees = eventAttendeeRepo.findAllByEventIdAndRsvpAcceptedFalseAndRsvpDeclinedFalse(eventId);
        Context context = new Context();
        context.setVariable("event", event);
        context.setVariable("decline", request.getDecline());
        for (EventAttendee attendee : attendees) {
            attendee.resetFlags();
            attendee.setAllowDecline(request.getDecline());
            String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .pathSegment("events", eventId.toString(), "reply-rsvp", attendee.getToken())
                    .build().toUri().toString();
            context.setVariable("url", url);
            String trackUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .pathSegment("api", "rsvp", attendee.getToken(), "copyright.png")
                    .build().toUri().toString();
            context.setVariable("trackUrl", trackUrl);
            String content = templateEngine.process("rsvp-invitation", context);
            Mail mail = mailRepo.findMailByAttendeeIdAndAndType(attendee.getId(), Mail.Type.RSVP);
            mail.setSubject(request.getSubject());
            mail.setBody(content);
            mail.setStatus(Mail.Status.QUEUED);
            mailService.queueMail(mail);
        }
        eventAttendeeRepo.saveAll(attendees);
    }

    private boolean isDevMode() {
        String[] activeProfiles = this.environment.getActiveProfiles();
        return List.of(activeProfiles).contains(DEV_PROFILE_NAME);
    }
}
