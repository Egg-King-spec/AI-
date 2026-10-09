package org.example.aispingboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.aispingboot.DTO.command.EmotionDiarySaveDTO;
import org.example.aispingboot.entity.EmotionDiary;
import org.example.aispingboot.exception.BusinessException;
import org.example.aispingboot.mapper.EmotionDiaryMapper;
import org.example.aispingboot.util.CurrentUserUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmotionDiaryService {

    private final EmotionDiaryMapper emotionDiaryMapper;

    public Page<EmotionDiary> page(Integer currentPage, Integer pageNum, Integer size, Integer pageSize,
                                   LocalDate startDate, LocalDate endDate, String emotionTag, boolean admin) {
        int pageNo = firstPositive(currentPage, pageNum, 1);
        int pageLimit = Math.min(firstPositive(size, pageSize, 10), 50);
        LambdaQueryWrapper<EmotionDiary> wrapper = new LambdaQueryWrapper<>();
        if (!admin) {
            wrapper.eq(EmotionDiary::getUserId, CurrentUserUtil.getUserId());
        }
        if (startDate != null) {
            wrapper.ge(EmotionDiary::getDiaryDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(EmotionDiary::getDiaryDate, endDate);
        }
        if (emotionTag != null && !emotionTag.isBlank()) {
            wrapper.eq(EmotionDiary::getEmotionTag, emotionTag);
        }
        wrapper.orderByDesc(EmotionDiary::getDiaryDate).orderByDesc(EmotionDiary::getCreatedAt);
        return emotionDiaryMapper.selectPage(new Page<>(pageNo, pageLimit), wrapper);
    }

    @Transactional
    public EmotionDiary save(EmotionDiarySaveDTO dto) {
        EmotionDiary diary = dto.getId() == null ? new EmotionDiary() : requireOwned(dto.getId());
        diary.setUserId(CurrentUserUtil.getUserId());
        diary.setDiaryDate(dto.getDiaryDate());
        diary.setEmotionTag(dto.getEmotionTag());
        diary.setEmotionScore(dto.getEmotionScore());
        diary.setContent(dto.getContent());
        diary.setAiAnalysis(dto.getAiAnalysis());
        diary.setRiskLevel(dto.getRiskLevel() == null ? 0 : dto.getRiskLevel());
        diary.setSuggestion(dto.getSuggestion());
        diary.setUpdatedAt(LocalDateTime.now());
        if (dto.getId() == null) {
            diary.setCreatedAt(LocalDateTime.now());
            emotionDiaryMapper.insert(diary);
        } else {
            emotionDiaryMapper.updateById(diary);
        }
        return diary;
    }

    @Transactional
    public void delete(Long id) {
        EmotionDiary diary = requireOwned(id);
        emotionDiaryMapper.deleteById(diary.getId());
    }

    private EmotionDiary requireOwned(Long id) {
        EmotionDiary diary = emotionDiaryMapper.selectById(id);
        if (diary == null) {
            throw new BusinessException("情绪日志不存在");
        }
        if (!diary.getUserId().equals(CurrentUserUtil.getUserId())) {
            throw new BusinessException("无权操作该情绪日志");
        }
        return diary;
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
