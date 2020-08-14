package com.fidecent.fbn.hx.dto.events;

import com.fidecent.fbn.hx.dto.groups.DistributionGroupDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Getter
@Setter
public class SendRsvpInvites {
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String replyTo;
    @NotEmpty
    private Set<DistributionGroupDto> groups;
    private boolean decline;
}
