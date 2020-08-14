package com.fidecent.fbn.hx.mappers;

import com.fidecent.fbn.hx.domain.Event;
import com.fidecent.fbn.hx.dto.events.CreateEventRequest;
import com.fidecent.fbn.hx.dto.events.EventDetails;
import com.fidecent.fbn.hx.dto.events.EventDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DataMapper {

    List<EventDto> mapEvents(List<Event> source);

    Event mapEventRequest(CreateEventRequest source);

    EventDetails mapEventDetails(Event event);
}
