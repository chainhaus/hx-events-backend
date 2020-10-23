package com.fidecent.fbn.hx.mappers;

import com.fidecent.fbn.hx.domain.Event;
import com.fidecent.fbn.hx.dto.events.CreateEventRequest;
import com.fidecent.fbn.hx.dto.events.EventDetails;
import com.fidecent.fbn.hx.dto.events.EventDto;
import org.mapstruct.Mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DataMapper {

    default List<EventDto> mapEvents(List<Event> source) {
        if (source != null) {
            return source.stream()
                    .map(this::mapEventDto)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    Event mapEventRequest(CreateEventRequest source);

    EventDetails mapEventDetails(Event event);

    EventDto mapEventDto(Event event);
}
