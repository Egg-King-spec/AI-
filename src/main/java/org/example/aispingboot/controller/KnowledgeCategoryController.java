package org.example.aispingboot.controller;

import org.example.aispingboot.common.Result;
import org.example.aispingboot.entity.KnowledgeCategory;
import org.example.aispingboot.service.KnowledgeCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge/category")
@RequiredArgsConstructor
public class KnowledgeCategoryController {

    private final KnowledgeCategoryService categoryService;

    @GetMapping("/tree")
    public Result<List<KnowledgeCategory>> tree() {
        return Result.ok(categoryService.tree());
    }
}
