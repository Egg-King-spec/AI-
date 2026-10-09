package org.example.aispingboot.controller;

import cn.hutool.json.JSONUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.aispingboot.AiService.PsychologicalSupportService;
import org.example.aispingboot.AiService.StructOutPut;
import org.example.aispingboot.DTO.command.ConsultationSessionCreateDTO;
import org.example.aispingboot.DTO.command.ConsultationStreamDTO;
import org.example.aispingboot.DTO.response.ConsultationMessageResponseDTO;
import org.example.aispingboot.DTO.response.ConsultationSessionListItemDTO;
import org.example.aispingboot.DTO.response.EmotionAnalysisResponseDTO;
import org.example.aispingboot.common.Result;
import org.example.aispingboot.common.ResultCode;
import org.example.aispingboot.service.ConsultationSessionService;
import org.example.aispingboot.service.EmotionAnalysisService;
import org.example.aispingboot.util.CurrentUserUtil;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/psychological-chat")
@RequiredArgsConstructor
public class PsychologicalChat {

    private final PsychologicalSupportService psychologicalSupportService;
    private final ConsultationSessionService consultationSessionService;
    private final EmotionAnalysisService emotionAnalysisService;

    @PostMapping("/session/start")
    public Result<StructOutPut.StreamChatSession> startSession(
            @Valid @RequestBody ConsultationSessionCreateDTO createDTO) {
        Long userId = CurrentUserUtil.getUserId();
        return Result.ok(psychologicalSupportService.startSession(userId, createDTO));
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@Valid @RequestBody ConsultationStreamDTO streamDTO) {
        Long userId = CurrentUserUtil.getUserId();
        boolean admin = CurrentUserUtil.isAdmin();
        if (userId == null) {
            return Flux.just(ServerSentEvent.<String>builder()
                    .event("error")
                    .data(JSONUtil.toJsonStr(Result.error(ResultCode.UNAUTHORIZED.getCode(),
                            ResultCode.UNAUTHORIZED.getMsg(), "用户未登录")))
                    .build());
        }
        return psychologicalSupportService.streamPsychologicalChat(
                        userId, admin, streamDTO.getSessionId(), streamDTO.getUserMessage())
                .map(fragment -> ServerSentEvent.<String>builder()
                        .event("message")
                        .data(JSONUtil.toJsonStr(Result.ok(Map.of("content", fragment, "type", "normal"))))
                        .build())
                .concatWith(Flux.just(ServerSentEvent.<String>builder()
                        .event("done")
                        .data("{}")
                        .build()))
                .delayElements(Duration.ofMillis(50));
    }

    @GetMapping("/sessions")
    public Result<org.example.aispingboot.DTO.response.PageResult<ConsultationSessionListItemDTO>> sessions(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.ok(org.example.aispingboot.DTO.response.PageResult.from(
                consultationSessionService.pageByUser(CurrentUserUtil.getUserId(), pageNum, pageSize)));
    }

    @GetMapping("/session/{sessionId}")
    public Result<List<ConsultationMessageResponseDTO>> sessionDetail(@PathVariable String sessionId) {
        return Result.ok(consultationSessionService.listMessages(
                sessionId, CurrentUserUtil.getUserId(), CurrentUserUtil.isAdmin()));
    }

    @DeleteMapping("/session/{sessionId}")
    public Result<?> deleteSession(@PathVariable String sessionId) {
        consultationSessionService.deleteSession(sessionId, CurrentUserUtil.getUserId(), CurrentUserUtil.isAdmin());
        return Result.ok();
    }

    @GetMapping("/session/{sessionId}/emotion")
    public Result<EmotionAnalysisResponseDTO> sessionEmotion(@PathVariable String sessionId) {
        Long sessionDbId = consultationSessionService.parseSessionId(sessionId);
        return Result.ok(emotionAnalysisService.analyze(
                sessionDbId, CurrentUserUtil.getUserId(), CurrentUserUtil.isAdmin()));
    }
}
