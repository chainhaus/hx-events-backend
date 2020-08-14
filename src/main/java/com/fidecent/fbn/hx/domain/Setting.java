package com.fidecent.fbn.hx.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Setting {

    @Id
    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private Key key;

    @Column(nullable = false)
    private String value;

    public enum Key {
        QUEUE_BATCH_SIZE, QUEUE_INTERVAL
    }
}
