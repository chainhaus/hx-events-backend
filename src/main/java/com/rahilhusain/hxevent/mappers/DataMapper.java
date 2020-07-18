package com.rahilhusain.hxevent.mappers;

import com.rahilhusain.hxevent.domain.Event;
import com.rahilhusain.hxevent.dto.events.CreateEventRequest;
import com.rahilhusain.hxevent.dto.events.EventDetails;
import com.rahilhusain.hxevent.dto.events.EventDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DataMapper {

    List<EventDto> mapEvents(List<Event> source);

    Event mapEventRequest(CreateEventRequest source);

    EventDetails mapEventDetails(Event event);
}
