package com.fidecent.fbn.hx.dto.rsvp;

public interface RsvpDto {

    Long getId();

    String getEmail();

    String getGroupName();

    String getCompanyName();

    String getFirstName();

    String getLastName();

    Boolean getRsvpAccepted();

    Boolean getRsvpMailSent();

    Boolean getRsvpMailFailed();

    Boolean getRsvpMailOpened();

    Boolean getRsvpDeclined();

    Boolean getRsvpApproved();

    Boolean getRsvpRejected();

    Boolean getApprovalMailSent();

    Boolean getApprovalMailFailed();

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
