package com.fidecent.fbn.hx.dto.events;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateEventRequest {
    @NotBlank
    @Length(max = 255)
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String additionalInfo;

    @NotBlank
    @Length(max = 255)
    private String speakerFirstName;

    @NotBlank
    @Length(max = 255)
    private String speakerLastName;

    @NotNull
    private LocalDate date;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;
}
