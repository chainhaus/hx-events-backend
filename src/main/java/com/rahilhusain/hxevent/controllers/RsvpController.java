package com.rahilhusain.hxevent.controllers;

import com.rahilhusain.hxevent.dto.ApproveRsvpRequest;
import com.rahilhusain.hxevent.dto.rsvp.RsvpDto;
import com.rahilhusain.hxevent.service.RsvpService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/rsvp")
public class RsvpController {
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


    @PostMapping("{invitationId}/reply")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void replyRsvp(@PathVariable String invitationId) {
        rsvpService.replyInvitation(invitationId);
    }

    @PostMapping("approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approveInvitation(@RequestBody @Valid ApproveRsvpRequest request) {
        rsvpService.approveInvitation(request);
    }
}
