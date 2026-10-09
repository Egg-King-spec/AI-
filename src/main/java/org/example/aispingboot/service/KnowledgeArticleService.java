package org.example.aispingboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.aispingboot.DTO.command.KnowledgeArticleSaveDTO;
import org.example.aispingboot.entity.KnowledgeArticle;
import org.example.aispingboot.entity.KnowledgeCategory;
import org.example.aispingboot.exception.BusinessException;
import org.example.aispingboot.mapper.KnowledgeArticleMapper;
import org.example.aispingboot.mapper.KnowledgeCategoryMapper;
import org.example.aispingboot.util.CurrentUserUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KnowledgeArticleService {

    private final KnowledgeArticleMapper articleMapper;
    private final KnowledgeCategoryMapper categoryMapper;

    public Page<KnowledgeArticle> page(Integer currentPage, Integer pageNum, Integer size, Integer pageSize,
                                       String title, Long categoryId, Integer status,
                                       String sortField, String sortDirection, boolean publicOnly) {
        int pageNo = firstPositive(currentPage, pageNum, 1);
        int pageLimit = Math.min(firstPositive(size, pageSize, 10), 50);
        LambdaQueryWrapper<KnowledgeArticle> wrapper = new LambdaQueryWrapper<>();
        if (publicOnly) {
            wrapper.eq(KnowledgeArticle::getStatus, 1);
        } else if (status != null) {
            wrapper.eq(KnowledgeArticle::getStatus, status);
        }
        if (StringUtils.hasText(title)) {
            wrapper.and(w -> w.like(KnowledgeArticle::getTitle, title)
                    .or().like(KnowledgeArticle::getSummary, title));
        }
        if (categoryId != null) {
            wrapper.eq(KnowledgeArticle::getCategoryId, categoryId);
        }
        String direction = "asc".equalsIgnoreCase(sortDirection) ? "asc" : "desc";
        String field = StringUtils.hasText(sortField) ? sortField : "updatedAt";
        if ("readCount".equals(field)) {
            wrapper.orderBy(true, "asc".equals(direction), KnowledgeArticle::getReadCount);
        } else if ("publishedAt".equals(field)) {
            wrapper.orderBy(true, "asc".equals(direction), KnowledgeArticle::getPublishedAt);
        } else {
            wrapper.orderBy(true, "asc".equals(direction), KnowledgeArticle::getUpdatedAt);
        }
        Page<KnowledgeArticle> result = articleMapper.selectPage(new Page<>(pageNo, pageLimit), wrapper);
        result.getRecords().forEach(this::enrich);
        return result;
    }

    public KnowledgeArticle detail(String id, boolean incrementReadCount) {
        KnowledgeArticle article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        if (incrementReadCount && Integer.valueOf(1).equals(article.getStatus())) {
            articleMapper.incrementReadCount(id);
            article.setReadCount((article.getReadCount() == null ? 0 : article.getReadCount()) + 1);
        }
        return enrich(article);
    }

    @Transactional
    public KnowledgeArticle create(KnowledgeArticleSaveDTO dto) {
        validateArticle(dto);
        KnowledgeArticle article = new KnowledgeArticle();
        article.setId(StringUtils.hasText(dto.getId())
                ? dto.getId()
                : UUID.randomUUID().toString().replace("-", ""));
        applyArticleFields(article, dto);
        article.setAuthorId(CurrentUserUtil.getUserId());
        article.setAuthorName(CurrentUserUtil.getUsername());
        article.setReadCount(0);
        article.setStatus(normalizeStatus(dto.getStatus(), 0));
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        if (Integer.valueOf(1).equals(article.getStatus())) {
            article.setPublishedAt(LocalDateTime.now());
        }
        articleMapper.insert(article);
        return enrich(article);
    }

    @Transactional
    public KnowledgeArticle update(String id, KnowledgeArticleSaveDTO dto) {
        validateArticle(dto);
        KnowledgeArticle article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        applyArticleFields(article, dto);
        Integer targetStatus = normalizeStatus(dto.getStatus(), article.getStatus());
        if (Integer.valueOf(1).equals(targetStatus) && article.getPublishedAt() == null) {
            article.setPublishedAt(LocalDateTime.now());
        }
        article.setStatus(targetStatus);
        article.setUpdatedAt(LocalDateTime.now());
        articleMapper.updateById(article);
        return enrich(article);
    }

    @Transactional
    public void updateStatus(String id, Integer status) {
        KnowledgeArticle article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        Integer targetStatus = normalizeStatus(status, null);
        if (targetStatus == null) {
            throw new BusinessException("文章状态不合法");
        }
        if (Integer.valueOf(1).equals(targetStatus) && article.getPublishedAt() == null) {
            article.setPublishedAt(LocalDateTime.now());
        }
        article.setStatus(targetStatus);
        article.setUpdatedAt(LocalDateTime.now());
        articleMapper.updateById(article);
    }

    @Transactional
    public void delete(String id) {
        if (articleMapper.deleteById(id) == 0) {
            throw new BusinessException("文章不存在");
        }
    }

    private void validateArticle(KnowledgeArticleSaveDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.getTitle()) || !StringUtils.hasText(dto.getContent())) {
            throw new BusinessException("文章标题和内容不能为空");
        }
        if (dto.getCategoryId() == null || categoryMapper.selectById(dto.getCategoryId()) == null) {
            throw new BusinessException("文章分类不存在");
        }
    }

    private void applyArticleFields(KnowledgeArticle article, KnowledgeArticleSaveDTO dto) {
        article.setTitle(dto.getTitle().trim());
        article.setSummary(dto.getSummary());
        article.setContent(dto.getContent());
        article.setCoverImage(dto.getCoverImage());
        article.setCategoryId(dto.getCategoryId());
        article.setTags(dto.getTags());
    }

    private KnowledgeArticle enrich(KnowledgeArticle article) {
        if (article == null) {
            return null;
        }
        KnowledgeCategory category = categoryMapper.selectById(article.getCategoryId());
        article.setCategoryName(category == null ? "未分类" : category.getCategoryName());
        article.setTagArray(parseTags(article.getTags()));
        return article;
    }

    private List<String> parseTags(String tags) {
        if (!StringUtils.hasText(tags)) {
            return List.of();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());
    }

    private Integer normalizeStatus(Integer status, Integer defaultStatus) {
        if (status == null) {
            return defaultStatus;
        }
        if (status < 0 || status > 2) {
            throw new BusinessException("文章状态不合法");
        }
        return status;
    }

    private int firstPositive(Integer first, Integer second, int defaultValue) {
        if (first != null && first > 0) {
            return first;
        }
        if (second != null && second > 0) {
            return second;
        }
        return defaultValue;
    }
}
