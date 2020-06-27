package com.rahilhusain.hxevent.mappers;

import com.microsoft.graph.models.extensions.DirectoryObject;
import com.microsoft.graph.models.extensions.EmailAddress;
import com.microsoft.graph.models.extensions.Group;
import com.microsoft.graph.models.extensions.Recipient;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.extensions.IDirectoryObjectCollectionWithReferencesPage;
import com.microsoft.graph.requests.extensions.IGroupCollectionPage;
import com.rahilhusain.hxevent.dto.groups.DistributionGroupDto;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    default Recipient mapMailRecipient(String address) {
        Recipient recipient = new Recipient();
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.address = address;
        recipient.emailAddress = emailAddress;
        return recipient;
    }

    default Set<String> mapMemberMailsResponse(IDirectoryObjectCollectionWithReferencesPage source) {
        List<DirectoryObject> page = source.getCurrentPage();
        return page.stream().map(d -> d.getRawObject().get("mail").getAsString()).collect(Collectors.toSet());
    }
}
