package com.fidecent.fbn.hx.service.impl;

import com.fidecent.fbn.hx.CommonUtils;
import com.fidecent.fbn.hx.configs.MDCFilter;
import com.fidecent.fbn.hx.domain.Event;
import com.fidecent.fbn.hx.domain.EventAttendee;
import com.fidecent.fbn.hx.domain.Mail;
import com.fidecent.fbn.hx.domain.NotificationRecipient;
import com.fidecent.fbn.hx.dto.UpdateRsvpRequest;
import com.fidecent.fbn.hx.dto.rsvp.RsvpDto;
import com.fidecent.fbn.hx.mappers.GraphMapper;
import com.fidecent.fbn.hx.repo.EventAttendeeRepo;
import com.fidecent.fbn.hx.repo.EventRepo;
import com.fidecent.fbn.hx.repo.NotificationRecipientRepo;
import com.fidecent.fbn.hx.service.GraphService;
import com.fidecent.fbn.hx.service.MailService;
import com.fidecent.fbn.hx.service.RsvpService;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.models.generated.ResponseType;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    private final TemplateEngine templateEngine;
    private final MailService mailService;
    private final NotificationRecipientRepo notificationRecipientRepo;
    private final MDCFilter mdcFilter;


    @Value("${hx-events.azure.time-zone}")
    private String timeZone;

    public RsvpServiceImpl(EventAttendeeRepo attendeeRepo, IAuthenticationProvider authenticationProvider, EventRepo eventRepo, GraphMapper mapper, TemplateEngine templateEngine, MailService mailService, NotificationRecipientRepo notificationRecipientRepo, MDCFilter mdcFilter) {
        this.attendeeRepo = attendeeRepo;
        this.authenticationProvider = authenticationProvider;
        this.eventRepo = eventRepo;
        this.mapper = mapper;
        this.templateEngine = templateEngine;
        this.mailService = mailService;
        this.notificationRecipientRepo = notificationRecipientRepo;
        this.mdcFilter = mdcFilter;
    }

    @Override
    public Page<RsvpDto> findAll(String search, Pageable pageable) {
        Page<RsvpDto> page;
        if (StringUtils.isNotBlank(search)) {
            page = attendeeRepo.findAllProjectedBy(StringUtils.wrap(search, '%'), pageable);
        } else {
            page = attendeeRepo.findAllProjectedBy(pageable);
        }
        updateAttendeeStatus(page);
        return page;
    }

    @Override
    public Page<RsvpDto> findByEventId(Long eventId, String search, Pageable pageable) {
        Page<RsvpDto> page;
        if (StringUtils.isNotBlank(search)) {
            page = attendeeRepo.findAllByEventId(eventId, StringUtils.wrap(search, '%'), pageable);
        } else {
            page = attendeeRepo.findAllByEventId(eventId, pageable);
        }
        updateAttendeeStatus(page);
        return page;
    }

    @Async
    @Override
    public void updateAttendeeStatus(Page<RsvpDto> page) {
        Map<Long, RsvpDto.EventDto> eventMap = new HashMap<>();
        page.get().filter(RsvpDto::getCalenderSent)
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
        List<EventAttendee> pendingAttendees = attendeeRepo.findAllByEventIdAndCalenderSentTrue(event.getId());
        pendingAttendees.forEach((entity) -> {
            ResponseType newStatus = attendees.get(entity.getEmail().toLowerCase());
            if (newStatus != null) {
                switch (newStatus) {
                    case ACCEPTED, TENTATIVELY_ACCEPTED -> {
                        if (!entity.getCalenderAccepted()) {
                            log.info("Updating status of attendee {} for the event {}-{} to CALENDER_ACCEPTED", entity.getEmail(), event.getId(), event.getTitle());
                            entity.setCalenderAccepted(true);
                        }
                    }
                    case DECLINED -> {
                        if (!entity.getCalenderDeclined()) {
                            log.info("Updating status of attendee {} for the event {}-{} to CALENDER_DECLINED", entity.getEmail(), event.getId(), event.getTitle());
                            entity.setCalenderDeclined(true);
                        }
                    }
                    case NONE, NOT_RESPONDED -> {
                        //ignore
                    }
                    default -> log.error("Unexpected calender invitation status {} of attendee {} for the event {}-{}", newStatus, entity.getEmail(), event.getId(), event.getTitle());
                }
                attendeeRepo.save(entity);
            } else {
                log.error("Attendee {} is in DB but not received from calender api for event {}-{}", entity.getEmail(), event.getId(), event.getTitle());
            }
        });
    }

    @Override
    @Transactional
    public void replyInvitation(String invitationToken, String reply) {
        EventAttendee attendee = findEventAttendee(invitationToken);
        mdcFilter.registerUsername(attendee.getEmail(), false);
        Event event = attendee.getEvent();
        if (attendee.getRsvpAccepted() || attendee.getRsvpDeclined()) {
            log.info("{} already responded for the event: {}-{}", attendee.getEmail(), event.getId(), event.getTitle());
            var msg = attendee.getRsvpAccepted() ? "RSVP_ACCEPTED" : "RSVP_DECLINED";
            throw new ResponseStatusException(HttpStatus.CONFLICT, msg);
        }
        if ("accept".equalsIgnoreCase(reply)) {
            attendee.setRsvpAccepted(true);
            String subject = "RSVP Alert";
            Context context = new Context();
            context.setVariable("event", event);
            context.setVariable("attendee", attendee);
            String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .pathSegment("rsvp", event.getId().toString(), attendee.getId().toString(), "approve")
                    .build().toUri().toString();
            context.setVariable("url", url);
            String content = templateEngine.process("rsvp-alert", context);
            List<NotificationRecipient> notificationRecipients = notificationRecipientRepo.findAll();
            if (notificationRecipients.isEmpty()) {
                log.warn("No recipients saved for the rsvp alert");
            } else {
                String recipients = notificationRecipients.stream().map(NotificationRecipient::getEmail).collect(Collectors.joining(";"));
                Mail mail = new Mail();
                mail.setSubject(subject);
                mail.setBody(content);
                mail.setAttendee(attendee);
                mail.setFromName("FBN Events");
                mail.setToAddress(recipients);
                mail.setType(Mail.Type.RSVP_ALERT);
                mailService.sendAsync(mail);
            }
        } else if ("decline".equalsIgnoreCase(reply)) {
            attendee.setRsvpDeclined(true);
        }
        log.info("{} {}ed the RSVP for the event: {}-{}", attendee.getEmail(), reply, event.getId(), event.getTitle());
        attendee.setRsvpMailOpened(true);
    }

    @Override
    @Transactional
    public void updateRsvpStatus(UpdateRsvpRequest request) {
        request.getInvitationIds().forEach((eventId, value) -> {
            Event event = eventRepo.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event Not found:" + eventId));
            List<EventAttendee> attendees = attendeeRepo.findAllById(value);
            String subject = "Confirmation for event - " + event.getTitle();
            Context context = new Context();
            context.setVariable("event", event);
            for (EventAttendee attendee : attendees) {
                context.setVariable("attendee", attendee);
                switch (request.getAction()) {
                    case FORCE_ACCEPT:
                        attendee.setRsvpAccepted(true);
                        attendee.setRsvpDeclined(false);
                    case APPROVE:
                        if (attendee.getRsvpApproved()) {
                            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invitation has already been approved");
                        } else if (attendee.getRsvpRejected()) {
                            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invitation has already been rejected");
                        }
                        attendee.setRsvpApproved(true);
                        String content = templateEngine.process("rsvp-approved", context);
                        Mail mail = new Mail();
                        mail.setSubject(subject);
                        mail.setBody(content);
                        mail.setAttendee(attendee);
                        mail.setFromName("FBN Events");
                        mail.setToAddress(attendee.getEmail());
                        mail.setType(Mail.Type.APPROVED);
                        mailService.queueMail(mail);
                        break;
                    case REJECT:
                        if (attendee.getRsvpApproved()) {
                            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invitation has already been approved");
                        } else if (attendee.getRsvpRejected()) {
                            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invitation has already been rejected");
                        }
                        attendee.setRsvpRejected(true);
                        break;
                    case FORCE_DECLINE:
                        attendee.setRsvpDeclined(true);
                        attendee.setRsvpAccepted(false);
                        attendee.setRsvpApproved(false);
                        attendee.setRsvpRejected(false);
                        break;
                }
                log.info("RSVP {}d for the attendee {} for the event: {}-{} by user {}", request.getAction(), attendee.getEmail(), event.getId(), event.getTitle(), CommonUtils.getLoggedInUser().orElse(null));
            }
        });
    }

    @Override
    @Async
    @Transactional
    public void sendCalenderInvite(Event event, List<EventAttendee> attendee) {
        if (event.getZoomOverride() != null && event.getZoomOverride()) {
            log.info("Zoom Override is enabled for the event: {}-{}. Skipping calender invitation for attendees: {}", event.getId(), event.getTitle(), attendee.stream().map(EventAttendee::getEmail).collect(Collectors.toList()));
            return;//No calender invite shall be sent if zoomOverride is enabled
        }
        String externalId = event.getExternalId();
        if (externalId == null) {
            Map<String, String> attendeeMap = new HashMap<>();
            attendee.forEach(a -> attendeeMap.put(a.getEmail(), a.getEmail()));
            com.microsoft.graph.models.extensions.Event calenderEvent = createCalenderEvent(event, attendeeMap);
            event.setExternalId(calenderEvent.id);
            eventRepo.save(event);
        } else {
            Set<String> emailIds = attendeeRepo.findAllInvitedAttendeesForCalenderEvent(externalId);
            attendee.stream().map(EventAttendee::getEmail).forEach(emailIds::add);
            Map<String, String> attendeeMap = emailIds.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
            updateCalenderEvent(externalId, attendeeMap);
        }
        attendee.forEach(a -> a.setCalenderSent(true));
        log.info("Calender invite sent to {} for the event: {}-{}", attendee.stream().map(EventAttendee::getEmail).collect(Collectors.toList()), event.getId(), event.getTitle());
        attendeeRepo.saveAll(attendee);
    }

    @Override
    @Transactional
    public void markOpened(String invitationToken) {
        EventAttendee attendee = findEventAttendee(invitationToken);
        mdcFilter.registerUsername(attendee.getEmail(), false);
        log.info("{} opened invitation mail for the event: {}-{}", attendee.getEmail(), attendee.getEvent().getId(), attendee.getEvent().getTitle());
        if (!attendee.getRsvpMailOpened()) {
            attendee.setRsvpMailOpened(true);
        }
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
                        .ifPresent(key -> map.put(key.toLowerCase(), Optional.of(attendee.status).map(s -> s.response).orElse(ResponseType.NONE))));
        return map;
    }

    public EventAttendee findEventAttendee(String invitationToken) {
        return attendeeRepo.findOneByToken(invitationToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invitation Not found."));
    }

}
