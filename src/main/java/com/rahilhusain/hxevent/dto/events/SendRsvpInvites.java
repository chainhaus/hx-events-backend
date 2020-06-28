package com.rahilhusain.hxevent.dto.events;

import com.rahilhusain.hxevent.dto.groups.DistributionGroupDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Getter
@Setter
public class SendRsvpInvites {
    @NotEmpty
    private Set<DistributionGroupDto> groups;
}
