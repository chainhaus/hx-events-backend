package com.fidecent.fbn.hx.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MailSetting {
    @NotNull
    @Min(25)
    @Max(1000)
    private Integer batchSize;

    @NotNull
    @Min(1)
    @Max(60)
    private Integer interval;//in minutes
}
