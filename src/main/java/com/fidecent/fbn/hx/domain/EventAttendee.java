package com.fidecent.fbn.hx.domain;

import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class EventAttendee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID token;

    @Column(nullable = false)
    private String email;

    private String firstName;

    private String lastName;

    private String companyName;

    @Column(nullable = false)
    private String groupName;

    @Formula(value = "SELECT count(*)>0 FROM MAIL m WHERE m.TYPE = 'RSVP' AND m.STATUS = 'SENT' AND m.ATTENDEE_ID = id")
    private Boolean rsvpMailSent;

    @Formula(value = "SELECT count(*)>0 FROM MAIL m WHERE m.TYPE = 'RSVP' AND m.STATUS = 'FAILED' AND m.ATTENDEE_ID = id")
    private Boolean rsvpMailFailed;

    @ColumnDefault("false")
    @Column(nullable = false, insertable = false)
    private Boolean rsvpAccepted;

    @ColumnDefault("false")
    @Column(nullable = false, insertable = false)
    private Boolean rsvpDeclined;

    @ColumnDefault("false")
    @Column(nullable = false, insertable = false)
    private Boolean rsvpApproved;

    @ColumnDefault("false")
    @Column(nullable = false, insertable = false)
    private Boolean rsvpRejected;

    @Formula(value = "SELECT count(*)>0 FROM MAIL m WHERE m.TYPE = 'APPROVED' AND m.STATUS = 'SENT' AND m.ATTENDEE_ID = id")
    private Boolean approvalMailSent;

    @Formula(value = "SELECT count(*)>0 FROM MAIL m WHERE m.TYPE = 'APPROVED' AND m.STATUS = 'FAILED' AND m.ATTENDEE_ID = id")
    private Boolean approvalMailFailed;

    @ColumnDefault("false")
    @Column(nullable = false, insertable = false)
    private Boolean calenderSent;

    @ColumnDefault("false")
    @Column(nullable = false, insertable = false)
    private Boolean calenderAccepted;

    @ColumnDefault("false")
    @Column(nullable = false, insertable = false)
    private Boolean calenderDeclined;

    @ManyToOne(optional = false)
    private Event event;

    public EventAttendee(String email, String companyName, String groupName, String firstName, String lastName) {
        this.token = UUID.randomUUID();
        this.email = email;
        this.companyName = companyName;
        this.groupName = groupName;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
