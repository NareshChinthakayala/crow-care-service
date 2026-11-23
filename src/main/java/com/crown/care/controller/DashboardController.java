package com.crown.care.controller;

import com.crown.care.dto.DashboardResponse;
import com.crown.care.entity.User;
import com.crown.care.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DashboardResponse dashboard(@AuthenticationPrincipal User admin) {
        return dashboardService.getDashboard(admin);
    }
}
