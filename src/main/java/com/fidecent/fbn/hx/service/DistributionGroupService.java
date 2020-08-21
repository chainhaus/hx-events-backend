package com.fidecent.fbn.hx.service;

import com.fidecent.fbn.hx.domain.EventAttendee;
import com.fidecent.fbn.hx.dto.groups.DistributionGroupDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface DistributionGroupService {
    Page<DistributionGroupDto> getAllDistributionGroups(Pageable pageable);

    List<EventAttendee> getAttendeesForDistributionGroups(Set<DistributionGroupDto> groups);
}
