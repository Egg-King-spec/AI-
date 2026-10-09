package org.example.aispingboot.controller;

import lombok.RequiredArgsConstructor;
import org.example.aispingboot.common.Result;
import org.example.aispingboot.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    @PreAuthorize("hasRole('2')")
    public Result<Map<String, Object>> overview() {
        return Result.ok(dashboardService.overview());
    }
}
