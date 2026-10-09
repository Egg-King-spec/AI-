package org.example.aispingboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.aispingboot.DTO.response.ConsultationMessageResponseDTO;
import org.example.aispingboot.entity.ConsultationMessage;
import org.example.aispingboot.mapper.ConsultationMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultationMessageService {

    @Autowired
    private ConsultationMessageMapper consultationMessageMapper;

    public ConsultationMessage saveUserMessage(Long sessionId, String content, String emotionTag) {
        ConsultationMessage userMessage = ConsultationMessage.builder()
                .sessionId(sessionId)
                .senderType(1)
                .messageType(1)
                .content(content)
                .emotionTag(emotionTag)
                .createdAt(LocalDateTime.now())
                .build();
        consultationMessageMapper.insert(userMessage);
        return userMessage;
    }

    public ConsultationMessage saveAimessage(Long sessionId, String content, String aiModel) {
        ConsultationMessage message = ConsultationMessage.builder()
                .sessionId(sessionId)
                .senderType(2)
                .messageType(1)
                .content(content)
                .aiModel(aiModel)
                .createdAt(LocalDateTime.now())
                .build();
        consultationMessageMapper.insert(message);
        return message;
    }

    public Integer getMessageCountBySessionId(Long sessionId) {
        return Math.toIntExact(consultationMessageMapper.selectCount(
                new LambdaQueryWrapper<ConsultationMessage>().eq(ConsultationMessage::getSessionId, sessionId)));
    }

    public ConsultationMessageResponseDTO getLastMessageBySessionId(Long sessionId) {
        ConsultationMessage lastMessage = getLastMessageEntity(sessionId);
        return lastMessage != null ? convertToResponseDTO(lastMessage) : null;
    }

    public ConsultationMessage getLastMessageEntity(Long sessionId) {
        return consultationMessageMapper.selectOne(new LambdaQueryWrapper<ConsultationMessage>()
                .eq(ConsultationMessage::getSessionId, sessionId)
                .orderByDesc(ConsultationMessage::getCreatedAt)
                .orderByDesc(ConsultationMessage::getId)
                .last("limit 1"));
    }

    public List<ConsultationMessage> listBySessionId(Long sessionId) {
        return consultationMessageMapper.selectList(new LambdaQueryWrapper<ConsultationMessage>()
                .eq(ConsultationMessage::getSessionId, sessionId)
                .orderByAsc(ConsultationMessage::getCreatedAt)
                .orderByAsc(ConsultationMessage::getId));
    }

    public List<ConsultationMessageResponseDTO> listResponsesBySessionId(Long sessionId) {
        return listBySessionId(sessionId).stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    @Transactional
    public void deleteBySessionId(Long sessionId) {
        consultationMessageMapper.delete(new LambdaQueryWrapper<ConsultationMessage>()
                .eq(ConsultationMessage::getSessionId, sessionId));
    }

    public ConsultationMessageResponseDTO convertToResponseDTO(ConsultationMessage message) {
        if (message == null) {
            return null;
        }
        ConsultationMessageResponseDTO responseDTO = new ConsultationMessageResponseDTO();
        responseDTO.setId(message.getId());
        responseDTO.setSessionId(message.getSessionId());
        responseDTO.setSenderType(message.getSenderType());
        responseDTO.setMessageType(message.getMessageType());
        responseDTO.setContent(message.getContent());
        responseDTO.setEmotionTag(message.getEmotionTag());
        responseDTO.setAiModel(message.getAiModel());
        responseDTO.setCreatedAt(message.getCreatedAt());
        responseDTO.setSenderTypeDesc(message.getSenderTypeDesc());
        responseDTO.setMessageTypeDesc(message.getMessageTypeDesc());
        responseDTO.calculateContentLength();
        return responseDTO;
    }
}
