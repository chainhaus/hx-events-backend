package com.rahilhusain.hxevent.mappers;

import com.google.gson.JsonElement;
import com.microsoft.graph.models.extensions.Attendee;
import com.microsoft.graph.models.extensions.DateTimeTimeZone;
import com.microsoft.graph.models.extensions.DirectoryObject;
import com.microsoft.graph.models.extensions.EmailAddress;
import com.microsoft.graph.models.extensions.Group;
import com.microsoft.graph.models.extensions.ItemBody;
import com.microsoft.graph.models.generated.AttendeeType;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.extensions.IDirectoryObjectCollectionWithReferencesPage;
import com.microsoft.graph.requests.extensions.IGroupCollectionPage;
import com.rahilhusain.hxevent.domain.Event;
import com.rahilhusain.hxevent.domain.EventAttendee;
import com.rahilhusain.hxevent.dto.groups.DistributionGroupDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring")
public interface GraphMapper {
    String SORT_PARAM = "$orderby";
    String COUNT_PARAM = "$count";
    String PAGE_SIZE_PARAM = "$top";
    String OFFSET_PARAM = "$skip";

    List<DistributionGroupDto> mapGroups(List<Group> groups);

    default Optional<QueryOption> mapSortParam(Sort sort) {
        return Optional.ofNullable(sort).filter(Sort::isSorted)
                .map(Sort::get)
                .map(a -> a.map(s -> String.join(" ", s.getProperty(), s.getDirection().name())).collect(Collectors.joining(",")))
                .map(value -> new QueryOption(SORT_PARAM, value));
    }

    default List<QueryOption> mapPageable(Pageable pageable) {
        List<QueryOption> list = new ArrayList<>(4);
        if (pageable.isPaged()) {
            list.add(new QueryOption(COUNT_PARAM, true));
            list.add(new QueryOption(PAGE_SIZE_PARAM, pageable.getPageSize()));
            list.add(new QueryOption(OFFSET_PARAM, pageable.getOffset()));
        }
        mapSortParam(pageable.getSort()).ifPresent(list::add);
        return list;
    }

    default Page<DistributionGroupDto> mapGroupResponse(IGroupCollectionPage page, Pageable pageable) {
        List<DistributionGroupDto> data = mapGroups(page.getCurrentPage());
        return applyPagination(pageable, data);
    }

    private <T> Page<T> applyPagination(Pageable pageable, List<T> data) {
        int totalElements = data.size();
        if (pageable.isPaged()) {
            int fromIndex = (int) pageable.getOffset();
            int toIndex = fromIndex + pageable.getPageSize();
            if (toIndex < totalElements) {
                data = data.subList(fromIndex, toIndex);
            }
        }
        return new PageImpl<>(data, pageable, totalElements);
    }


    default Stream<EventAttendee> mapMemberMailsResponse(IDirectoryObjectCollectionWithReferencesPage source, String groupName) {
        List<DirectoryObject> page = source.getCurrentPage();
        return page.stream().map(d -> {
            String email = d.getRawObject().get("mail").getAsString();
            JsonElement companyName = d.getRawObject().get("companyName");
            return new EventAttendee(email, companyName.isJsonNull() ? null : companyName.getAsString(), groupName);
        });
    }

    default Attendee mapAttendee(String email, String name) {
        Attendee attendee = new Attendee();
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.address = email;
        emailAddress.name = name;
        attendee.emailAddress = emailAddress;
        attendee.type = AttendeeType.REQUIRED;
        return attendee;
    }

    default DateTimeTimeZone mapDateTime(LocalDate date, LocalTime time, String timeZone) {
        DateTimeTimeZone dateTimeTimeZone = new DateTimeTimeZone();
        dateTimeTimeZone.dateTime = date.atTime(time).format(DateTimeFormatter.ISO_DATE_TIME);
        dateTimeTimeZone.timeZone = timeZone;
        return dateTimeTimeZone;
    }

    @SuppressWarnings("UnmappedTargetProperties")
    @Mapping(target = "subject", source = "title")
    @Mapping(target = "body", source = "description")
    com.microsoft.graph.models.extensions.Event mapCalenderEvent(Event source);

    @SuppressWarnings("UnmappedTargetProperties")
    @Mapping(target = "content", source = ".")
    @Mapping(target = "contentType", constant = "TEXT")
    ItemBody mapItemBody(String body);
}
