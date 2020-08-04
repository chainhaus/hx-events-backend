package com.rahilhusain.hxevent.dto.events;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Immutable
@Subselect("select " +
        "e.ID as ID, " +
        "e.CREATED_DATE as CREATED_DATE, " +
        "e.title as TITLE," +
        "(select count(*) from EVENT_ATTENDEE a join MAIL m on m.ATTENDEE_ID = a.ID and m.TYPE = 'RSVP' where a.EVENT_ID = e.ID and a.STATUS = 'RSVP_SENT' and m.STATUS != 'SENT') as RSVP_QUEUED , " +
        "(select count(*) from EVENT_ATTENDEE a join MAIL m on m.ATTENDEE_ID = a.ID and m.TYPE = 'RSVP' where a.EVENT_ID = e.ID and m.STATUS = 'SENT') as RSVP_SENT , " +
        "(select count(*) from EVENT_ATTENDEE a join MAIL m on m.ATTENDEE_ID = a.ID and m.TYPE = 'APPROVED' where a.EVENT_ID = e.ID and a.STATUS = 'RSVP_APPROVED' and m.STATUS != 'SENT') as CONFIRMATION_QUEUED, " +
        "(select count(*) from EVENT_ATTENDEE a join MAIL m on m.ATTENDEE_ID = a.ID and m.TYPE = 'APPROVED' where a.EVENT_ID = e.ID and m.STATUS = 'SENT') as CONFIRMATION_SENT " +
        "from EVENT e")
public class EventStatistic {
    @Id
    private Long id;
    private String title;
    private LocalDate createdDate;
    private Integer rsvpQueued;
    private Integer rsvpSent;
    private Integer confirmationQueued;
    private Integer confirmationSent;
}
