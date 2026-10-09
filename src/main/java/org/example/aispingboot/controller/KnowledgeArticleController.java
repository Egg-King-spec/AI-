package org.example.aispingboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.aispingboot.DTO.command.KnowledgeArticleSaveDTO;
import org.example.aispingboot.DTO.command.KnowledgeArticleStatusDTO;
import org.example.aispingboot.common.Result;
import org.example.aispingboot.entity.KnowledgeArticle;
import org.example.aispingboot.exception.BusinessException;
import org.example.aispingboot.service.KnowledgeArticleService;
import org.example.aispingboot.util.CurrentUserUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/knowledge/article")
@RequiredArgsConstructor
public class KnowledgeArticleController {

    private final KnowledgeArticleService articleService;

    @GetMapping("/page")
    public Result<Page<KnowledgeArticle>> page(
            @RequestParam(required = false) Integer currentPage,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortDirection) {
        boolean publicOnly = status == null && !CurrentUserUtil.isAdmin();
        return Result.ok(articleService.page(currentPage, pageNum, size, pageSize, title, categoryId,
                status, sortField, sortDirection, publicOnly));
    }

    @GetMapping("/{id}")
    public Result<KnowledgeArticle> detail(@PathVariable String id) {
        boolean admin = CurrentUserUtil.isAdmin();
        KnowledgeArticle article = articleService.detail(id, !admin);
        if (!admin && !Integer.valueOf(1).equals(article.getStatus())) {
            throw new BusinessException("文章不存在或未发布");
        }
        return Result.ok(article);
    }

    @PostMapping
    @PreAuthorize("hasRole('2')")
    public Result<KnowledgeArticle> create(@Valid @RequestBody KnowledgeArticleSaveDTO dto) {
        return Result.ok(articleService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('2')")
    public Result<KnowledgeArticle> update(@PathVariable String id,
                                           @Valid @RequestBody KnowledgeArticleSaveDTO dto) {
        return Result.ok(articleService.update(id, dto));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('2')")
    public Result<?> updateStatus(@PathVariable String id,
                                  @Valid @RequestBody KnowledgeArticleStatusDTO dto) {
        articleService.updateStatus(id, dto.getStatus());
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('2')")
    public Result<?> delete(@PathVariable String id) {
        articleService.delete(id);
        return Result.ok();
    }
}
