package com.fidecent.fbn.hx.controllers;

import com.fidecent.fbn.hx.dto.UpdateRsvpRequest;
import com.fidecent.fbn.hx.dto.rsvp.RsvpDto;
import com.fidecent.fbn.hx.service.RsvpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/rsvp")
public class RsvpController {

    @Value("classpath:/copyright.png")
    private Resource copyrightFile;

    private final RsvpService rsvpService;

    public RsvpController(RsvpService rsvpService) {
        this.rsvpService = rsvpService;
    }

    @GetMapping
    public Page<RsvpDto> findAll(Pageable pageable) {
        return rsvpService.findAll(pageable);
    }

    @GetMapping("{eventId}")
    public Page<RsvpDto> findByEventId(Pageable pageable, @PathVariable Long eventId) {
        return rsvpService.findByEventId(eventId, pageable);
    }

    @PostMapping("{invitationToken}/reply/{reply:accept|decline}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void replyRsvp(@PathVariable String invitationToken, @PathVariable String reply) {
        rsvpService.replyInvitation(invitationToken, reply);
    }

    @GetMapping(path = "{invitationToken}/copyright.png", produces = MediaType.IMAGE_PNG_VALUE)
    public Resource onMailInvitationOpened(@PathVariable String invitationToken) {
        rsvpService.markOpened(invitationToken);
        return copyrightFile;
    }

    @PostMapping("update-status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRsvpStatus(@RequestBody @Valid UpdateRsvpRequest request) {
        rsvpService.updateRsvpStatus(request);
    }
}
