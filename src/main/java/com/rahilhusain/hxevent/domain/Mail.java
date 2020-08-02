package com.rahilhusain.hxevent.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.Instant;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Mail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    @org.hibernate.annotations.Type(type="text")
    private String body;

    @Column(nullable = false)
    private String fromName;

    @Column(nullable = false)
    private String toAddress;

    private String replyToAddress;

    @ManyToOne(optional = false)
    private EventAttendee attendee;

    @CreatedDate
    @Column(nullable = false)
    private Instant createdDate;

    @ColumnDefault("'QUEUED'")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, insertable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    public enum Status {
        QUEUED, SENT, FAILED
    }

    public enum Type {
        RSVP, APPROVED
    }
}
