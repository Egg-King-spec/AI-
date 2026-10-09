package org.example.aispingboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.aispingboot.entity.KnowledgeCategory;
import org.example.aispingboot.mapper.KnowledgeCategoryMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KnowledgeCategoryService {

    private final KnowledgeCategoryMapper categoryMapper;

    public List<KnowledgeCategory> tree() {
        List<KnowledgeCategory> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<KnowledgeCategory>()
                        .eq(KnowledgeCategory::getStatus, 1)
                        .orderByAsc(KnowledgeCategory::getSortOrder)
                        .orderByAsc(KnowledgeCategory::getId));
        Map<Long, KnowledgeCategory> byId = new LinkedHashMap<>();
        for (KnowledgeCategory category : categories) {
            category.setChildren(new ArrayList<>());
            byId.put(category.getId(), category);
        }
        List<KnowledgeCategory> roots = new ArrayList<>();
        for (KnowledgeCategory category : categories) {
            Long parentId = category.getParentId();
            if (parentId == null || parentId == 0L || !byId.containsKey(parentId)) {
                roots.add(category);
            } else {
                byId.get(parentId).getChildren().add(category);
            }
        }
        return roots;
    }
}
