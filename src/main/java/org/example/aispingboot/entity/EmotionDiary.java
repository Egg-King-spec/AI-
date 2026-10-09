package org.example.aispingboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("emotion_diary")
public class EmotionDiary {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private LocalDate diaryDate;

    private String emotionTag;

    private Integer emotionScore;

    private String content;

    private String aiAnalysis;

    private Integer riskLevel;

    private String suggestion;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
