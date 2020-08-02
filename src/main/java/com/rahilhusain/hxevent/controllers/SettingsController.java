package com.rahilhusain.hxevent.controllers;

import com.rahilhusain.hxevent.domain.DisplayName;
import com.rahilhusain.hxevent.domain.ReplyTo;
import com.rahilhusain.hxevent.service.SettingsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping("display-name")
    public List<DisplayName> getDisplayNames() {
        return settingsService.getMailDisplayNames();
    }

    @PostMapping("display-name")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addDisplayName(@RequestBody @Valid DisplayName request) {
        settingsService.addDisplayName(request);
    }

    @DeleteMapping("display-name/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addDisplayName(@PathVariable Integer id) {
        settingsService.deleteDisplayName(id);
    }

    @GetMapping("reply-to")
    public List<ReplyTo> getReplyToList() {
        return settingsService.getReplyToList();
    }

    @PostMapping("reply-to")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addReplyTo(@RequestBody @Valid ReplyTo request) {
        settingsService.addReplyTo(request);
    }

    @DeleteMapping("reply-to/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addReplyTo(@PathVariable Integer id) {
        settingsService.deleteReplyTo(id);
    }

}
