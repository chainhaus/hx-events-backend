package com.fidecent.fbn.hx.service.impl;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.http.GraphServiceException;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.extensions.IContactCollectionPage;
import com.microsoft.graph.requests.extensions.IContactFolderCollectionPage;
import com.microsoft.graph.requests.extensions.IContactFolderCollectionRequest;
import com.fidecent.fbn.hx.domain.EventAttendee;
import com.fidecent.fbn.hx.dto.groups.DistributionGroupDto;
import com.fidecent.fbn.hx.mappers.GraphMapper;
import com.fidecent.fbn.hx.service.DistributionGroupService;
import com.fidecent.fbn.hx.service.GraphService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
public class DistributionGroupServiceImpl implements DistributionGroupService, GraphService {
    @Getter
    private final IAuthenticationProvider authenticationProvider;
    private final GraphMapper mapper;

    public DistributionGroupServiceImpl(IAuthenticationProvider authenticationProvider, GraphMapper mapper) {
        this.authenticationProvider = authenticationProvider;
        this.mapper = mapper;
    }

    @Override
    public Page<DistributionGroupDto> getAllDistributionGroups(Pageable pageable) {
        //paging not supported for the groups resource
        List<QueryOption> options = mapper.mapPageable(pageable);
        IContactFolderCollectionRequest folderCollectionRequest = this.getGraphClient()
                .me()
                .contactFolders()
                .buildRequest(options);
        try {
            IContactFolderCollectionPage collectionPage = folderCollectionRequest.get();
            return mapper.mapContactFolderResponse(collectionPage, pageable);
        } catch (GraphServiceException e) {
            log.catching(e);
            throw new ResponseStatusException(HttpStatus.valueOf(e.getResponseCode()), e.getMessage());
        }
    }

    @Override
    public List<EventAttendee> getAttendeesForDistributionGroups(Set<DistributionGroupDto> groups) {
        return groups.stream().flatMap(group -> {
            IContactCollectionPage page = this.getGraphClient().me()
                    .contactFolders(group.getId())
                    .contacts()
                    .buildRequest()
                    .top(1000)
                    .get();
            List<EventAttendee> data = mapper.mapMemberMailsResponse(page, group.getDisplayName());
            while (page.getNextPage() != null) {
                page = page.getNextPage().buildRequest().get();
                data.addAll(mapper.mapMemberMailsResponse(page, group.getDisplayName()));
            }
            return data.stream();
        }).collect(Collectors.toList());
    }
}
