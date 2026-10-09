package org.example.aispingboot.service;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.example.aispingboot.DTO.response.EmotionAnalysisResponseDTO;
import org.example.aispingboot.entity.ConsultationMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmotionAnalysisService {

    private static final List<String> POSITIVE_WORDS = List.of("开心", "高兴", "放松", "希望", "期待", "感谢", "不错", "好转", "喜欢", "平静");
    private static final List<String> NEGATIVE_WORDS = List.of("难过", "焦虑", "压力", "失眠", "抑郁", "孤独", "崩溃", "害怕", "紧张", "烦躁", "累", "痛苦");
    private static final List<String> CRISIS_WORDS = List.of("不想活", "自杀", "结束生命", "轻生", "割腕", "跳楼", "活不下去");

    private final ConsultationSessionService consultationSessionService;
    private final ConsultationMessageService consultationMessageService;

    @Transactional
    public EmotionAnalysisResponseDTO analyze(Long sessionId, Long userId, boolean admin) {
        consultationSessionService.requireAccessibleSession(sessionId, userId, admin);
        List<ConsultationMessage> messages = consultationMessageService.listBySessionId(sessionId);
        StringBuilder textBuilder = new StringBuilder();
        int start = Math.max(0, messages.size() - 20);
        for (int i = start; i < messages.size(); i++) {
            ConsultationMessage message = messages.get(i);
            if (message.getSenderType() != null && message.getSenderType() == 1) {
                textBuilder.append(message.getContent()).append('\n');
            }
        }
        String text = textBuilder.toString().toLowerCase(Locale.ROOT);
        int positive = countMatches(text, POSITIVE_WORDS);
        int negative = countMatches(text, NEGATIVE_WORDS);
        int crisis = countMatches(text, CRISIS_WORDS);
        int score = clamp(50 + positive * 6 - negative * 8 - crisis * 40, 0, 100);
        int riskLevel = crisis > 0 ? 3 : negative >= 5 ? 2 : negative > 0 ? 1 : 0;
        String primaryEmotion = determineEmotion(text, crisis, negative, positive, score);
        boolean isNegative = riskLevel > 0 || score < 45;
        List<String> suggestions = buildSuggestions(riskLevel, negative, positive);
        EmotionAnalysisResponseDTO response = EmotionAnalysisResponseDTO.builder()
                .primaryEmotion(primaryEmotion)
                .emotionScore(score)
                .isNegative(isNegative)
                .riskLevel(riskLevel)
                .suggestion(suggestions.isEmpty() ? "继续保持记录，观察自己的情绪变化。" : suggestions.get(0))
                .improvementSuggestions(suggestions)
                .riskDescription(riskDescription(riskLevel))
                .build();
        consultationSessionService.updateEmotionAnalysis(sessionId, JSONUtil.toJsonStr(response));
        return response;
    }

    private String determineEmotion(String text, int crisis, int negative, int positive, int score) {
        if (crisis > 0) {
            return "危机";
        }
        if (text.contains("焦虑") || text.contains("紧张") || text.contains("压力")) {
            return "焦虑";
        }
        if (text.contains("难过") || text.contains("抑郁") || text.contains("痛苦")) {
            return "悲伤";
        }
        if (text.contains("孤独")) {
            return "孤独";
        }
        if (positive > negative && score >= 55) {
            return "积极";
        }
        if (negative > 0) {
            return "低落";
        }
        return "中性";
    }

    private List<String> buildSuggestions(int riskLevel, int negative, int positive) {
        List<String> suggestions = new ArrayList<>();
        if (riskLevel >= 3) {
            suggestions.add("请立即联系可信任的人、学校心理中心或当地紧急援助热线，不要独自承受。");
            suggestions.add("把当前感受告诉身边的人，并尽量不要独处。");
            return suggestions;
        }
        if (negative > 0) {
            suggestions.add("先做一次缓慢呼吸，把此刻最强烈的感受写下来。");
            suggestions.add("尝试和一个可信任的人聊十分钟，不需要立刻解决问题。");
            suggestions.add("如果情绪持续影响睡眠和生活，建议预约学校心理咨询。");
        } else if (positive > 0) {
            suggestions.add("记录今天让你感觉好一点的具体事情，帮助自己积累积极体验。");
            suggestions.add("保持规律作息和适度运动，让好的状态更稳定。");
        } else {
            suggestions.add("可以继续写几句今天发生了什么，帮助自己更清楚地理解情绪。");
        }
        return suggestions;
    }

    private String riskDescription(int riskLevel) {
        return switch (riskLevel) {
            case 3 -> "检测到可能的危机表达，建议尽快寻求现实中的专业支持。";
            case 2 -> "近期负向情绪较集中，建议关注睡眠、食欲和社交状态。";
            case 1 -> "当前存在一些负向情绪，可以先从记录和倾诉开始。";
            default -> "当前未发现明显高风险信号。";
        };
    }

    private int countMatches(String text, List<String> words) {
        int count = 0;
        for (String word : words) {
            if (text.contains(word)) {
                count++;
            }
        }
        return count;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
