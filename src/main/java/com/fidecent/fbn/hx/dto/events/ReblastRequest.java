package com.fidecent.fbn.hx.dto.events;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ReblastRequest {
    @NotBlank
    private String description;
}
