package org.example.aispingboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.aispingboot.DTO.command.ConsultationSessionCreateDTO;
import org.example.aispingboot.DTO.response.ConsultationMessageResponseDTO;
import org.example.aispingboot.DTO.response.ConsultationSessionListItemDTO;
import org.example.aispingboot.entity.ConsultationMessage;
import org.example.aispingboot.entity.ConsultationSession;
import org.example.aispingboot.entity.User;
import org.example.aispingboot.exception.BusinessException;
import org.example.aispingboot.mapper.ConsultationSessionMapper;
import org.example.aispingboot.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultationSessionService {

    private static final DateTimeFormatter TITLE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM-dd HH:mm");

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ConsultationSessionMapper consultationSessionMapper;

    @Autowired
    private ConsultationMessageService consultationMessageService;

    @Transactional
    public ConsultationSession createSession(Long userId, ConsultationSessionCreateDTO createDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        ConsultationSession session = ConsultationSession.builder()
                .userId(userId)
                .sessionTitle(StringUtils.hasText(createDTO.getSessionTitle())
                        ? createDTO.getSessionTitle()
                        : "宁渡AI助手 - " + LocalDateTime.now().format(TITLE_TIME_FORMATTER))
                .startedAt(LocalDateTime.now())
                .build();
        consultationSessionMapper.insert(session);
        return session;
    }

    public Page<ConsultationSessionListItemDTO> pageByUser(Long userId, int pageNum, int pageSize) {
        Page<ConsultationSession> page = consultationSessionMapper.selectPage(
                new Page<>(Math.max(pageNum, 1), normalizePageSize(pageSize)),
                new LambdaQueryWrapper<ConsultationSession>()
                        .eq(ConsultationSession::getUserId, userId)
                        .orderByDesc(ConsultationSession::getStartedAt));
        return toPage(page, false);
    }

    public Page<ConsultationSessionListItemDTO> pageAll(int pageNum, int pageSize) {
        Page<ConsultationSession> page = consultationSessionMapper.selectPage(
                new Page<>(Math.max(pageNum, 1), normalizePageSize(pageSize)),
                new LambdaQueryWrapper<ConsultationSession>()
                        .orderByDesc(ConsultationSession::getStartedAt));
        return toPage(page, true);
    }

    public List<ConsultationMessageResponseDTO> listMessages(String sessionId, Long userId, boolean admin) {
        Long dbSessionId = parseSessionId(sessionId);
        requireAccessibleSession(dbSessionId, userId, admin);
        return consultationMessageService.listResponsesBySessionId(dbSessionId);
    }

    @Transactional
    public void deleteSession(String sessionId, Long userId, boolean admin) {
        Long dbSessionId = parseSessionId(sessionId);
        ConsultationSession session = requireAccessibleSession(dbSessionId, userId, admin);
        consultationMessageService.deleteBySessionId(dbSessionId);
        consultationSessionMapper.deleteById(session.getId());
    }

    public ConsultationSession requireAccessibleSession(Long sessionId, Long userId, boolean admin) {
        ConsultationSession session = consultationSessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException("会话不存在");
        }
        if (!admin && !session.getUserId().equals(userId)) {
            throw new BusinessException("无权访问该会话");
        }
        return session;
    }

    public void updateEmotionAnalysis(Long sessionId, String analysisJson) {
        ConsultationSession session = consultationSessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException("会话不存在");
        }
        session.setLastEmotionAnalysis(analysisJson);
        session.setLastEmotionUpdatedAt(LocalDateTime.now());
        consultationSessionMapper.updateById(session);
    }

    public Long parseSessionId(String sessionId) {
        if (!StringUtils.hasText(sessionId)) {
            throw new BusinessException("会话ID不能为空");
        }
        String idText = sessionId.startsWith("session_") ? sessionId.substring("session_".length()) : sessionId;
        try {
            return Long.parseLong(idText);
        } catch (NumberFormatException e) {
            throw new BusinessException("会话ID格式错误");
        }
    }

    private Page<ConsultationSessionListItemDTO> toPage(Page<ConsultationSession> source, boolean includeUser) {
        Page<ConsultationSessionListItemDTO> result = new Page<>(source.getCurrent(), source.getSize(), source.getTotal());
        result.setRecords(source.getRecords().stream()
                .map(session -> toListItem(session, includeUser))
                .collect(Collectors.toList()));
        return result;
    }

    private ConsultationSessionListItemDTO toListItem(ConsultationSession session, boolean includeUser) {
        ConsultationMessageResponseDTO lastMessage = consultationMessageService.getLastMessageBySessionId(session.getId());
        long messageCount = consultationMessageService.getMessageCountBySessionId(session.getId());
        LocalDateTime endTime = lastMessage == null || lastMessage.getCreatedAt() == null
                ? LocalDateTime.now()
                : lastMessage.getCreatedAt();
        long durationMinutes = Math.max(0, Duration.between(session.getStartedAt(), endTime).toMinutes());
        User user = includeUser ? userMapper.selectById(session.getUserId()) : null;
        return ConsultationSessionListItemDTO.builder()
                .id(session.getId())
                .userId(session.getUserId())
                .username(user == null ? null : user.getUsername())
                .sessionTitle(session.getSessionTitle())
                .startedAt(session.getStartedAt())
                .lastMessageContent(lastMessage == null ? "" : lastMessage.getContent())
                .messageCount(messageCount)
                .durationMinutes(durationMinutes)
                .status("ACTIVE")
                .build();
    }

    private int normalizePageSize(int pageSize) {
        if (pageSize <= 0) {
            return 10;
        }
        return Math.min(pageSize, 50);
    }
}
