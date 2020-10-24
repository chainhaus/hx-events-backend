package com.fidecent.fbn.hx.dto.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventDetails extends EventDto {
    private String description;
    private Boolean zoomOverride;
    private String emailSubject;
}
