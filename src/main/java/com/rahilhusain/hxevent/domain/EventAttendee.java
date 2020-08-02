package com.rahilhusain.hxevent.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class EventAttendee {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String email;

    private String companyName;

    @Column(nullable = false)
    private String groupName;

    @Column(nullable = false, insertable = false)
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'RSVP_SENT'")
    private Status status;

    @ManyToOne(optional = false)
    private Event event;

    @SuppressWarnings("unused")
    public enum Status {
        RSVP_SENT, RSVP_ACCEPTED, RSVP_DECLINED, RSVP_APPROVED, CALENDER_SENT, CALENDER_ACCEPTED, CALENDER_DECLINED
    }

    public EventAttendee(String email, String companyName, String groupName) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.companyName = companyName;
        this.groupName = groupName;
    }
}
