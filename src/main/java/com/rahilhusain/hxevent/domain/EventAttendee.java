package com.rahilhusain.hxevent.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

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

    private String companyName;

    @Column(nullable = false)
    private String groupName;

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

    public EventAttendee(String email, String companyName, String groupName) {
        this.token = UUID.randomUUID();
        this.email = email;
        this.companyName = companyName;
        this.groupName = groupName;
    }
}
