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
@TableName("knowledge_category")
public class KnowledgeCategory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String categoryName;

    private Long parentId;

    private Integer sortOrder;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private List<KnowledgeCategory> children = new ArrayList<>();
}
