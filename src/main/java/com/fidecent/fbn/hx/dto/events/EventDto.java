package com.fidecent.fbn.hx.dto.events;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class EventDto {
    private Long id;
    private String externalId;
    private String title;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String createdBy;
    private String openedInvitationPercent;
}
