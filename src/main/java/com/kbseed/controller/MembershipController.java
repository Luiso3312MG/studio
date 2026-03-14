package com.kbseed.controller;

import com.kbseed.dto.*;
import com.kbseed.service.MembershipService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memberships")
public class MembershipController {
    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @GetMapping("/plans")
    public List<MembershipPlanDTO> getPlans() { return membershipService.getPlans(); }

    @GetMapping("/overview")
    public List<MembershipOverviewDTO> getOverview() { return membershipService.getOverview(); }

    @GetMapping("/client/{clientId}")
    public ClientMembershipDTO getCurrentMembership(@PathVariable Long clientId) {
        return membershipService.getCurrentMembershipForClient(clientId);
    }

    @PostMapping("/client/{clientId}/activate")
    public ClientMembershipDTO activate(@PathVariable Long clientId, @RequestBody ActivateMembershipRequest request) {
        return membershipService.activateOrRenew(clientId, request);
    }

    @PutMapping("/{membershipId}/disciplines")
    public ClientMembershipDTO updateDisciplines(@PathVariable Long membershipId, @RequestBody UpdateMembershipDisciplinesRequest request) {
        return membershipService.updateDisciplines(membershipId, request);
    }
}
