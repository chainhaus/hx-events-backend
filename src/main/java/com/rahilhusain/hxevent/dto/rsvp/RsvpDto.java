package com.rahilhusain.hxevent.dto.rsvp;

public interface RsvpDto {

    Long getId();

    String getEmail();

    String getGroupName();

    String getCompanyName();

    String getFirstName();

    String getLastName();

    Boolean getRsvpAccepted();

    Boolean getRsvpDeclined();

    Boolean getRsvpApproved();

    Boolean getRsvpRejected();

    Boolean getCalenderSent();

    Boolean getCalenderAccepted();

    Boolean getCalenderDeclined();

    EventDto getEvent();

    interface EventDto {

        Long getId();

        String getExternalId();

        String getTitle();
    }
}
