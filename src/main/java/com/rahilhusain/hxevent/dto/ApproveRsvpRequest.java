package com.rahilhusain.hxevent.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class ApproveRsvpRequest {
    private Map<Long, Set<String>> invitationIds;
}
