package com.rahilhusain.hxevent.service.impl;

import com.rahilhusain.hxevent.domain.Event;
import com.rahilhusain.hxevent.domain.EventAttendee;
import com.rahilhusain.hxevent.dto.events.CreateEventRequest;
import com.rahilhusain.hxevent.dto.events.EventDetails;
import com.rahilhusain.hxevent.dto.events.EventDto;
import com.rahilhusain.hxevent.mappers.DataMapper;
import com.rahilhusain.hxevent.repo.EventAttendeeRepo;
import com.rahilhusain.hxevent.repo.EventRepo;
import com.rahilhusain.hxevent.service.DistributionGroupService;
import com.rahilhusain.hxevent.service.EventService;
import com.rahilhusain.hxevent.service.MailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepo eventRepo;
    private final DataMapper dataMapper;
    private final DistributionGroupService groupService;
    private final MailService mailService;
    private final SpringTemplateEngine templateEngine;
    private final EventAttendeeRepo eventAttendeeRepo;

    public EventServiceImpl(EventRepo eventRepo, DataMapper dataMapper, DistributionGroupService groupService, MailService mailService, SpringTemplateEngine templateEngine, EventAttendeeRepo eventAttendeeRepo) {
        this.eventRepo = eventRepo;
        this.dataMapper = dataMapper;
        this.groupService = groupService;
        this.mailService = mailService;
        this.templateEngine = templateEngine;
        this.eventAttendeeRepo = eventAttendeeRepo;
    }

    @Override
    public Page<EventDto> findAll(Pageable pageable) {
        Page<Event> page = eventRepo.findAll(pageable);
        List<EventDto> content = dataMapper.mapEvents(page.getContent());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    public EventDetails create(CreateEventRequest request) {
        //TODO: create event on outlook calender
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
    public void sendRsvpInvites(Long eventId, Set<String> groupIds) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        Set<String> emailIds = groupService.getEmailIdsForGroups(groupIds);
        String subject = "Invitation for event - " + event.getTitle();
        Context context = new Context();
        context.setVariable("event", event);
        for (String emailId : emailIds) {
            EventAttendee attendee = new EventAttendee(emailId, event);
            eventAttendeeRepo.save(attendee);
            String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .pathSegment("/events", eventId.toString(), "/reply-rsvp", attendee.getId().toString())
                    .build().toUri().toString();
            context.setVariable("url", url);
            String content = templateEngine.process("rsvp-invitation", context);
            //TODO: send to actual email
            mailService.sendEmail("rahilhusain166@gmail.com", subject, content);
        }

    }

    @Override
    @Transactional
    public void replyRsvp(Long eventId, String token) {
        EventAttendee attendee = eventAttendeeRepo.findById(UUID.fromString(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Token"));
        if (attendee.getStatus() == EventAttendee.Status.RSVP) {
            attendee.setStatus(EventAttendee.Status.RSVP_REPLIED);
            eventAttendeeRepo.save(attendee);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, EventAttendee.Status.RSVP_REPLIED.name());
        }
    }
}
