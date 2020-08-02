package com.rahilhusain.hxevent.service;

import com.microsoft.graph.models.extensions.Attendee;
import com.rahilhusain.hxevent.domain.EventAttendee;
import com.rahilhusain.hxevent.dto.groups.DistributionGroupDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DistributionGroupService {
    Page<DistributionGroupDto> getAllDistributionGroups(Pageable pageable);

    List<EventAttendee> getAttendeesForDistributionGroups(Set<DistributionGroupDto> groups);
}
