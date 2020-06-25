package com.rahilhusain.hxevent.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class User {
    @Id
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    private String firstName;
    private String lastName;
    @Column(nullable = false)
    private String password;
}
