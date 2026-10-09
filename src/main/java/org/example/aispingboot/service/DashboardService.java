package org.example.aispingboot.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.aispingboot.entity.ConsultationSession;
import org.example.aispingboot.entity.EmotionDiary;
import org.example.aispingboot.entity.KnowledgeArticle;
import org.example.aispingboot.mapper.ConsultationMessageMapper;
import org.example.aispingboot.mapper.ConsultationSessionMapper;
import org.example.aispingboot.mapper.EmotionDiaryMapper;
import org.example.aispingboot.mapper.KnowledgeArticleMapper;
import org.example.aispingboot.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserMapper userMapper;
    private final ConsultationSessionMapper sessionMapper;
    private final ConsultationMessageMapper messageMapper;
    private final KnowledgeArticleMapper articleMapper;
    private final EmotionDiaryMapper emotionDiaryMapper;

    public Map<String, Object> overview() {
        Map<String, Object> result = new HashMap<>();
        result.put("userCount", userMapper.selectCount(null));
        result.put("sessionCount", sessionMapper.selectCount(null));
        result.put("messageCount", messageMapper.selectCount(null));
        result.put("articleCount", articleMapper.selectCount(null));
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        result.put("todaySessionCount", sessionMapper.selectCount(
                new LambdaQueryWrapper<ConsultationSession>().ge(ConsultationSession::getStartedAt, todayStart)));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        List<String> chartDates = new ArrayList<>();
        List<Long> chartSessions = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            chartDates.add(day.format(formatter));
            chartSessions.add(sessionMapper.selectCount(new LambdaQueryWrapper<ConsultationSession>()
                    .ge(ConsultationSession::getStartedAt, day.atStartOfDay())
                    .lt(ConsultationSession::getStartedAt, day.plusDays(1).atStartOfDay())));
        }
        result.put("chartDates", chartDates);
        result.put("chartSessions", chartSessions);

        Map<String, Integer> emotionDistribution = new LinkedHashMap<>();
        for (ConsultationSession session : sessionMapper.selectList(null)) {
            String emotion = "未分析";
            if (session.getLastEmotionAnalysis() != null) {
                try {
                    JSONObject object = JSONUtil.parseObj(session.getLastEmotionAnalysis());
                    Object primaryEmotion = object.get("primaryEmotion");
                    emotion = primaryEmotion == null ? "未分析" : String.valueOf(primaryEmotion);
                } catch (Exception ignored) {
                    emotion = "未分析";
                }
            }
            emotionDistribution.merge(emotion, 1, Integer::sum);
        }
        for (EmotionDiary diary : emotionDiaryMapper.selectList(null)) {
            String tag = diary.getEmotionTag() == null ? "未标记" : diary.getEmotionTag();
            emotionDistribution.merge(tag, 1, Integer::sum);
        }
        result.put("emotionDistribution", emotionDistribution);
        result.put("draftArticleCount", articleMapper.selectCount(
                new LambdaQueryWrapper<KnowledgeArticle>().eq(KnowledgeArticle::getStatus, 0)));
        return result;
    }
}
