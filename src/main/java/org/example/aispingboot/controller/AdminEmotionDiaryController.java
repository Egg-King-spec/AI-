package org.example.aispingboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.aispingboot.common.Result;
import org.example.aispingboot.entity.EmotionDiary;
import org.example.aispingboot.service.EmotionDiaryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/emotion-diaries")
@RequiredArgsConstructor
public class AdminEmotionDiaryController {

    private final EmotionDiaryService emotionDiaryService;

    @GetMapping
    @PreAuthorize("hasRole('2')")
    public Result<Page<EmotionDiary>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String emotionTag) {
        return Result.ok(emotionDiaryService.page(null, pageNum, null, pageSize,
                startDate, endDate, emotionTag, true));
    }
}
