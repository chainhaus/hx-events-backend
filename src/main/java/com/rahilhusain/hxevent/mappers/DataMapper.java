package com.rahilhusain.hxevent.mappers;

import com.rahilhusain.hxevent.domain.Event;
import com.rahilhusain.hxevent.domain.EventAttendee;
import com.rahilhusain.hxevent.domain.User;
import com.rahilhusain.hxevent.dto.events.CreateEventRequest;
import com.rahilhusain.hxevent.dto.events.EventDetails;
import com.rahilhusain.hxevent.dto.events.EventDto;
import com.rahilhusain.hxevent.dto.rsvp.RsvpDto;
import com.rahilhusain.hxevent.security.LoggedInUser;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DataMapper {

    LoggedInUser mapUser(User source);

    List<EventDto> mapEvents(List<Event> source);

    default String mapName(User source) {
        if (source == null) return null;
        String firstName = source.getFirstName();
        String lastName = source.getLastName();
        if (firstName == null && lastName == null) return source.getEmail();
        if (lastName == null) return firstName;
        if (firstName == null) return lastName;
        return String.join(" ", firstName, lastName);
    }

    Event mapEventRequest(CreateEventRequest source);

    EventDetails mapEventDetails(Event event);

    User mapToEntity(LoggedInUser source);
}
