package com.rahilhusain.hxevent.service.impl;

import com.rahilhusain.hxevent.domain.DisplayName;
import com.rahilhusain.hxevent.repo.DisplayNameRepo;
import com.rahilhusain.hxevent.service.SettingsService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SettingsServiceImpl implements SettingsService {
    private final DisplayNameRepo displayNameRepo;

    public SettingsServiceImpl(DisplayNameRepo displayNameRepo) {
        this.displayNameRepo = displayNameRepo;
    }

    @Override
    public List<DisplayName> getMailDisplayNames() {
        return displayNameRepo.findAll();
    }

    @Override
    public void addDisplayName(DisplayName request) {
        if (displayNameRepo.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Display Name already exists");
        }
        displayNameRepo.save(request);
    }

    @Override
    public void deleteDisplayName(Integer id) {
        displayNameRepo.deleteById(id);
    }
}
