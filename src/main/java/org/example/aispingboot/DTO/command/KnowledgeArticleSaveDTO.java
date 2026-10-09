package org.example.aispingboot.DTO.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class KnowledgeArticleSaveDTO {

    private String id;

    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "文章标题最多200个字符")
    private String title;

    @Size(max = 1000, message = "文章摘要最多1000个字符")
    private String summary;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    private String coverImage;

    @NotNull(message = "请选择文章分类")
    private Long categoryId;

    @Size(max = 500, message = "文章标签最多500个字符")
    private String tags;

    private Integer status;
}
