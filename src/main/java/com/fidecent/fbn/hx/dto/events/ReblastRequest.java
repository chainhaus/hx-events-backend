package com.fidecent.fbn.hx.dto.events;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ReblastRequest {
    @NotBlank
    private String description;
    @NotNull
    private Boolean decline;
}
