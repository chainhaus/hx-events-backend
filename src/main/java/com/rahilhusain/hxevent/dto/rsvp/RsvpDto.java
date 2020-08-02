package com.rahilhusain.hxevent.dto.rsvp;

import com.rahilhusain.hxevent.domain.EventAttendee;

import java.util.UUID;

public interface RsvpDto {

    UUID getId();

    String getEmail();

    String getGroupName();

    String getCompanyName();

    EventAttendee.Status getStatus();

    EventDto getEvent();

    interface EventDto {

        Long getId();

        String getExternalId();

        String getTitle();
    }
}
