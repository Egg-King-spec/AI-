package org.example.aispingboot.AiService;

import org.example.aispingboot.DTO.command.ConsultationSessionCreateDTO;
import org.example.aispingboot.DTO.response.ConsultationMessageResponseDTO;
import org.example.aispingboot.entity.ConsultationSession;
import org.example.aispingboot.service.ConsultationMessageService;
import org.example.aispingboot.service.ConsultationSessionService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PsychologicalSupportService {

    @Autowired
    @Qualifier("open-ai")
    private ChatClient chatClient;

    @Autowired
    private ChatMemory chatMemory;

    @Autowired
    private ConsultationSessionService consultationSessionService;

    @Autowired
    private ConsultationMessageService consultationMessageService;

    @Transactional
    public StructOutPut.StreamChatSession startSession(Long userId, ConsultationSessionCreateDTO createDTO) {
        ConsultationSession consultationSession = consultationSessionService.createSession(userId, createDTO);
        consultationMessageService.saveUserMessage(consultationSession.getId(), createDTO.getInitialMessage(), null);
        String sessionId = "session_" + consultationSession.getId();
        return new StructOutPut.StreamChatSession(
                sessionId,
                userId,
                createDTO.getInitialMessage(),
                System.currentTimeMillis(),
                System.currentTimeMillis() + 86400000L,
                1,
                "ACTIVE"
        );
    }

    public Flux<String> streamPsychologicalChat(Long userId, boolean admin, String sessionId, String userMessage) {
        return Flux.create(sink -> {
            Long dbSessionId = consultationSessionService.parseSessionId(sessionId);
            consultationSessionService.requireAccessibleSession(dbSessionId, userId, admin);
            boolean isInitialMessage = false;
            Integer messageCount = consultationMessageService.getMessageCountBySessionId(dbSessionId);
            if (messageCount == 1) {
                ConsultationMessageResponseDTO lastMessage = consultationMessageService.getLastMessageBySessionId(dbSessionId);
                if (lastMessage != null && lastMessage.getSenderType() == 1
                        && userMessage.equals(lastMessage.getContent())) {
                    isInitialMessage = true;
                }
            }
            if (!isInitialMessage) {
                consultationMessageService.saveUserMessage(dbSessionId, userMessage, null);
            }

            String conversationId = "conversation_" + sessionId;
            List<Message> userMessages = new ArrayList<>();
            userMessages.add(new UserMessage(userMessage));
            chatMemory.add(conversationId, userMessages);
            Prompt prompt = new Prompt(List.of(
                    new SystemMessage(PromptManage.PSYCHOLOGICAL_SUPPORT_SYSTEM_PROMPT)
            ));

            StringBuilder fullResponse = new StringBuilder();
            AtomicReference<Disposable> upstream = new AtomicReference<>();
            sink.onCancel(() -> {
                Disposable subscription = upstream.get();
                if (subscription != null) {
                    subscription.dispose();
                }
            });

            Disposable subscription = chatClient.prompt(prompt)
                    .user(userMessage)
                    .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                    .stream()
                    .content()
                    .doOnNext(fragment -> {
                        fullResponse.append(fragment);
                        sink.next(fragment);
                    })
                    .doOnComplete(() -> {
                        String completeRes = fullResponse.toString();
                        consultationMessageService.saveAimessage(dbSessionId, completeRes, "deepseek");
                        List<Message> aiMessages = new ArrayList<>();
                        aiMessages.add(new AssistantMessage(completeRes));
                        chatMemory.add(conversationId, aiMessages);
                        sink.complete();
                    })
                    .doOnError(sink::error)
                    .subscribe();
            upstream.set(subscription);
        });
    }
}
