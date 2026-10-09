package org.example.aispingboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName("knowledge_article")
public class KnowledgeArticle {

    @TableId(type = IdType.INPUT)
    private String id;

    private String title;

    private String summary;

    private String content;

    private String coverImage;

    private Long categoryId;

    private String tags;

    private Integer status;

    private Long authorId;

    private String authorName;

    private Integer readCount;

    private LocalDateTime publishedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String categoryName;

    @TableField(exist = false)
    private List<String> tagArray = new ArrayList<>();
}
