package org.example.aispingboot.DTO.command;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmotionDiarySaveDTO {

    private Long id;

    @NotNull(message = "日记日期不能为空")
    private LocalDate diaryDate;

    @NotBlank(message = "情绪标签不能为空")
    @Size(max = 30, message = "情绪标签最多30个字符")
    private String emotionTag;

    @NotNull(message = "情绪分数不能为空")
    @Min(value = 0, message = "情绪分数不能小于0")
    @Max(value = 100, message = "情绪分数不能大于100")
    private Integer emotionScore;

    @NotBlank(message = "日记内容不能为空")
    @Size(max = 5000, message = "日记内容最多5000个字符")
    private String content;

    private String aiAnalysis;

    @Min(value = 0, message = "风险等级不能小于0")
    @Max(value = 3, message = "风险等级不能大于3")
    private Integer riskLevel;

    @Size(max = 500, message = "建议最多500个字符")
    private String suggestion;
}
