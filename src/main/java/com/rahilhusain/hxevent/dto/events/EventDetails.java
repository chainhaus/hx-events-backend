package com.rahilhusain.hxevent.dto.events;

import lombok.Data;

@Data
public class EventDetails extends EventDto {
    private String description;
    private String speakerFirstName;
    private String speakerLastName;
}
