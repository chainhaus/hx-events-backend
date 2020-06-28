package com.rahilhusain.hxevent.service.impl;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.http.GraphServiceException;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.extensions.IDirectoryObjectCollectionWithReferencesPage;
import com.microsoft.graph.requests.extensions.IGroupCollectionPage;
import com.microsoft.graph.requests.extensions.IGroupCollectionRequest;
import com.rahilhusain.hxevent.dto.groups.DistributionGroupDto;
import com.rahilhusain.hxevent.mappers.GraphMapper;
import com.rahilhusain.hxevent.service.DistributionGroupService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

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
        List<QueryOption> options = new ArrayList<>(2);
        options.add(new QueryOption("$filter", "startswith(mail,'dlist')"));
        mapper.mapSortParam(pageable.getSort()).ifPresent(options::add);
        IGroupCollectionRequest request = this.getGraphClient().groups()
                .buildRequest(options)
                .select("id,mail,displayName,description");
        try {
            IGroupCollectionPage page = request.get();
            return mapper.mapGroupResponse(page, pageable);
        } catch (GraphServiceException e) {
            log.catching(e);
            throw new ResponseStatusException(HttpStatus.valueOf(e.getResponseCode()), e.getMessage());
        }
    }

    @Override
    public Map<String, Set<String>> getEmailIdsForGroups(Set<DistributionGroupDto> groups) {
        Map<String, Set<String>> map = new HashMap<>();
        groups.forEach(group -> {
            IDirectoryObjectCollectionWithReferencesPage page = this.getGraphClient().groups(group.getId())
                    .transitiveMembers()
                    .buildRequest()
                    .select("mail")
                    .get();
            Set<String> emails = map.computeIfAbsent(group.getDisplayName(), (key) -> new HashSet<>());
            emails.addAll(mapper.mapMemberMailsResponse(page));
        });
        return map;
    }
}
