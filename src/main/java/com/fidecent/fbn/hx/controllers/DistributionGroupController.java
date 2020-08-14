package com.fidecent.fbn.hx.controllers;

import com.fidecent.fbn.hx.dto.groups.DistributionGroupDto;
import com.fidecent.fbn.hx.service.DistributionGroupService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/distribution-groups")
public class DistributionGroupController {

    private final DistributionGroupService groupService;

    public DistributionGroupController(DistributionGroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public Page<DistributionGroupDto> getDistributionGroups(Pageable pageable) {
        return groupService.getAllDistributionGroups(pageable);
    }
}
