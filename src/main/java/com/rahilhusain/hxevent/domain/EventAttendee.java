package com.rahilhusain.hxevent.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
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

    @Column(nullable = false)
    private String groupName;

    @Column(nullable = false, insertable = false)
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'RSVP'")
    private Status status;

    @ManyToOne(optional = false)
    private Event event;

    public enum Status {
        RSVP, RSVP_REPLIED, RSVP_ACCEPTED, CALENDER_SENT, CALENDER_ACCEPTED, CALENDER_REJECTED
    }

    public EventAttendee(String email, Event event, String groupName) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.event = event;
        this.groupName = groupName;
    }
}
