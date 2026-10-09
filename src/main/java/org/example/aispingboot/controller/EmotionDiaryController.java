package org.example.aispingboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.aispingboot.DTO.command.EmotionDiarySaveDTO;
import org.example.aispingboot.common.Result;
import org.example.aispingboot.entity.EmotionDiary;
import org.example.aispingboot.service.EmotionDiaryService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/emotion-diary")
@RequiredArgsConstructor
public class EmotionDiaryController {

    private final EmotionDiaryService emotionDiaryService;

    @GetMapping("/page")
    public Result<Page<EmotionDiary>> page(
            @RequestParam(required = false) Integer currentPage,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String emotionTag) {
        return Result.ok(emotionDiaryService.page(currentPage, pageNum, size, pageSize,
                startDate, endDate, emotionTag, false));
    }

    @PostMapping
    public Result<EmotionDiary> save(@Valid @RequestBody EmotionDiarySaveDTO dto) {
        return Result.ok(emotionDiaryService.save(dto));
    }

    @PutMapping("/{id}")
    public Result<EmotionDiary> update(@PathVariable Long id,
                                       @Valid @RequestBody EmotionDiarySaveDTO dto) {
        dto.setId(id);
        return Result.ok(emotionDiaryService.save(dto));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        emotionDiaryService.delete(id);
        return Result.ok();
    }
}
