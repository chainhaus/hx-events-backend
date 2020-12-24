package com.fidecent.fbn.hx.dto.rsvp;

import com.fidecent.fbn.hx.dto.events.EventDetails;
import lombok.Value;

@Value
public class InvitationDetails {
    EventDetails event;
    Boolean allowDecline;
}
