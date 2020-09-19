package com.fidecent.fbn.hx.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.text.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1024)
    private String externalId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    @Type(type="text")
    private String description;

    @Column(nullable = false, length = 50)
    private String speakerFirstName;

    @Column(nullable = false, length = 50)
    private String speakerLastName;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @CreatedDate
    @Column(nullable = false)
    private Instant createdDate;

    @CreatedBy
    @Column(nullable = false)
    private String createdBy;

    @Formula("select count(*) from event_attendee a where a.event_id = id and a.rsvp_mail_opened = true")
    private Integer openedInvitations;

    @Formula("select count(*) from event_attendee a where a.event_id = id")
    private Integer totalInvitations;

    public String getOpenedInvitationPercent() {
        return DecimalFormat.getPercentInstance().format(((double) (openedInvitations == null ? 0 : openedInvitations)) / (totalInvitations == null || totalInvitations == 0 ? 1 : totalInvitations));
    }

}
