package org.example.aispingboot.DTO.response;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class EmotionAnalysisResponseDTO {

    private String primaryEmotion;

    private Integer emotionScore;

    private Boolean isNegative;

    private Integer riskLevel;

    private String suggestion;

    @Builder.Default
    private List<String> improvementSuggestions = new ArrayList<>();

    private String riskDescription;
}
