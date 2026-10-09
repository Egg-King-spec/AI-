package org.example.aispingboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.aispingboot.DTO.response.ConsultationSessionListItemDTO;
import org.example.aispingboot.common.Result;
import org.example.aispingboot.service.ConsultationSessionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/consultations")
@RequiredArgsConstructor
public class AdminConsultationController {

    private final ConsultationSessionService consultationSessionService;

    @GetMapping
    @PreAuthorize("hasRole('2')")
    public Result<Page<ConsultationSessionListItemDTO>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.ok(consultationSessionService.pageAll(pageNum, pageSize));
    }
}
