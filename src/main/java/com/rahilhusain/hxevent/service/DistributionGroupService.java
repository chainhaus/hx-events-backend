package com.rahilhusain.hxevent.service;

import com.rahilhusain.hxevent.dto.groups.DistributionGroupDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface DistributionGroupService {
    Page<DistributionGroupDto> getAllDistributionGroups(Pageable pageable);

    Set<String> getEmailIdsForGroups(Set<String> groupIds);
}
