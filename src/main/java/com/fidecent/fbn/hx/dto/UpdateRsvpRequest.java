package com.fidecent.fbn.hx.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class UpdateRsvpRequest {
    private Map<Long, Set<Long>> invitationIds;
    private Action action;

    public enum Action {
        APPROVE, REJECT, FORCE_ACCEPT, FORCE_DECLINE
    }
}
