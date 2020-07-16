package com.rahilhusain.hxevent.service.impl;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.models.generated.ResponseType;
import com.rahilhusain.hxevent.domain.Event;
import com.rahilhusain.hxevent.domain.EventAttendee;
import com.rahilhusain.hxevent.domain.EventAttendee.Status;
import com.rahilhusain.hxevent.dto.ApproveRsvpRequest;
import com.rahilhusain.hxevent.dto.rsvp.RsvpDto;
import com.rahilhusain.hxevent.mappers.GraphMapper;
import com.rahilhusain.hxevent.repo.EventAttendeeRepo;
import com.rahilhusain.hxevent.repo.EventRepo;
import com.rahilhusain.hxevent.service.RsvpService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Service
public class RsvpServiceImpl implements RsvpService, GraphService {
    @Getter
    private final IAuthenticationProvider authenticationProvider;
    private final EventRepo eventRepo;
    private final EventAttendeeRepo attendeeRepo;
    private final GraphMapper mapper;


    @Value("${hx-events.azure.time-zone}")
    private String timeZone;

    public RsvpServiceImpl(EventAttendeeRepo attendeeRepo, IAuthenticationProvider authenticationProvider, EventRepo eventRepo, GraphMapper mapper) {
        this.attendeeRepo = attendeeRepo;
        this.authenticationProvider = authenticationProvider;
        this.eventRepo = eventRepo;
        this.mapper = mapper;
    }

    @Override
    public Page<RsvpDto> findAll(Pageable pageable) {
        Page<RsvpDto> page = attendeeRepo.findAllProjectedBy(pageable);
        updateAttendeeStatus(page);
        return page;
    }

    @Override
    public Page<RsvpDto> findByEventId(Long eventId, Pageable pageable) {
        Page<RsvpDto> page = attendeeRepo.findAllByEventId(eventId, pageable);
        updateAttendeeStatus(page);
        return page;
    }

    @Async
    @Override
    public void updateAttendeeStatus(Page<RsvpDto> page) {
        Map<Long, RsvpDto.EventDto> eventMap = new HashMap<>();
        page.get().filter(attendee -> attendee.getStatus() == Status.RSVP_ACCEPTED || attendee.getStatus() == Status.CALENDER_SENT)
                .map(RsvpDto::getEvent)
                .filter(e -> e.getExternalId() != null)
                .forEach(e -> eventMap.put(e.getId(), e));
        eventMap.values()
                .forEach(this::updateAttendeeStatus);
    }

    @Async
    @Override
    @Transactional
    public void updateAttendeeStatus(RsvpDto.EventDto event) {
        Map<String, ResponseType> attendees = getCalenderEventAttendeesStatus(event.getExternalId());
        List<EventAttendee> pendingAttendees = attendeeRepo.findAllByEventIdAndStatusIn(event.getId(), Status.RSVP_ACCEPTED, Status.CALENDER_SENT);
        pendingAttendees.forEach((entity) -> {
            ResponseType newStatus = attendees.get(entity.getEmail());
            if (newStatus != null) {
                switch (newStatus) {
                    case ACCEPTED, TENTATIVELY_ACCEPTED -> {
                        log.info("Updating status of attendee {} for the event {} to {}", entity.getEmail(), event.getTitle(), Status.CALENDER_ACCEPTED);
                        entity.setStatus(Status.CALENDER_ACCEPTED);
                    }
                    case DECLINED -> {
                        log.info("Updating status of attendee {} for the event {} to {}", entity.getEmail(), event.getTitle(), Status.CALENDER_REJECTED);
                        entity.setStatus(Status.CALENDER_REJECTED);
                    }
                }
                if (entity.getStatus() == Status.RSVP_ACCEPTED) {
                    log.info("Updating status of attendee {} for the event {} to {}", entity.getEmail(), event.getTitle(), Status.CALENDER_SENT);
                    entity.setStatus(Status.CALENDER_SENT);
                }
                attendeeRepo.save(entity);
            } else {
                log.error("Attendee {} is in DB but not received from calender api for event {} - {}", entity.getEmail(), event.getId(), event.getTitle());
            }
        });
    }

    @Override
    public void replyInvitation(String invitationId) {
        updateStatus(invitationId, Status.RSVP_REPLIED, Status.RSVP);
    }

    @Override
    public void approveInvitation(ApproveRsvpRequest request) {
        request.getInvitationIds().forEach((eventId, value) -> {
            Event event = eventRepo.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event Not found:" + eventId));
            List<UUID> invitationIds = value.stream().map(UUID::fromString).collect(Collectors.toList());
            List<EventAttendee> attendees = attendeeRepo.findAllById(invitationIds);
            attendees.forEach(attendee -> updateAttendeeStatus(Status.RSVP_ACCEPTED, Status.RSVP_REPLIED, attendee));
            sendCalenderInvite(event, attendees);
        });
    }

    @Override
    @Async
    @Transactional
    public void sendCalenderInvite(Event event, List<EventAttendee> attendee) {
        String externalId = event.getExternalId();
        if (externalId == null) {
            Map<String, String> attendeeMap = new HashMap<>();
            attendee.forEach(a -> attendeeMap.put(a.getEmail(), a.getEmail()));
            com.microsoft.graph.models.extensions.Event calenderEvent = createCalenderEvent(event, attendeeMap);
            event.setExternalId(calenderEvent.id);
        } else {
            Set<String> emailIds = attendeeRepo.findAllApprovedAttendeesForCalenderEvent(externalId);
            Map<String, String> attendeeMap = emailIds.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
            updateCalenderEvent(externalId, attendeeMap);
        }
        attendee.forEach(a -> a.setStatus(Status.CALENDER_SENT));
        attendeeRepo.saveAll(attendee);
    }

    public com.microsoft.graph.models.extensions.Event createCalenderEvent(Event event, Map<String, String> attendee) {
        com.microsoft.graph.models.extensions.Event calenderEvent = mapper.mapCalenderEvent(event);
        calenderEvent.start = mapper.mapDateTime(event.getDate(), event.getStartTime(), timeZone);
        calenderEvent.end = mapper.mapDateTime(event.getDate(), event.getEndTime(), timeZone);
        log.info("Creating calender event {}({}), with attendees {}", event.getTitle(), event.getId(), attendee);
        calenderEvent.attendees = attendee.entrySet()
                .stream()
                .map(a -> mapper.mapAttendee(a.getKey(), a.getValue()))
                .collect(Collectors.toList());
        return getGraphClient().me().events()
                .buildRequest()
                .post(calenderEvent);
    }

    public void updateCalenderEvent(String eventId, Map<String, String> attendee) {
        com.microsoft.graph.models.extensions.Event calenderEvent = new com.microsoft.graph.models.extensions.Event();
        log.info("Updating attendees of calender event {}, with attendees {}", eventId, attendee);
        calenderEvent.attendees = attendee.entrySet()
                .stream()
                .map(a -> mapper.mapAttendee(a.getKey(), a.getValue()))
                .collect(Collectors.toList());
        getGraphClient().me().events(eventId)
                .buildRequest()
                .patch(calenderEvent);
    }

    public Map<String, ResponseType> getCalenderEventAttendeesStatus(String eventId) {
        Map<String, ResponseType> map = new HashMap<>();
        getGraphClient().me().events(eventId)
                .buildRequest()
                .select("attendees")
                .get().attendees
                .forEach(attendee -> Optional.ofNullable(attendee.emailAddress)
                        .map(add -> add.address)
                        .ifPresent(key -> map.put(key, Optional.of(attendee.status).map(s -> s.response).orElse(ResponseType.NONE))));
        return map;
    }

    @Transactional
    public void updateStatus(String invitationId, Status newStatus, Status oldStatus) {
        UUID uuid;
        try {
            uuid = UUID.fromString(invitationId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invitation Not found.");
        }
        EventAttendee attendee = attendeeRepo.findById(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invitation Not found."));
        updateAttendeeStatus(newStatus, oldStatus, attendee);
    }

    public void updateAttendeeStatus(Status newStatus, Status oldStatus, EventAttendee attendee) {
        if (attendee.getStatus() == oldStatus) {
            attendee.setStatus(newStatus);
            attendeeRepo.save(attendee);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, attendee.getStatus().name());
        }
    }

}