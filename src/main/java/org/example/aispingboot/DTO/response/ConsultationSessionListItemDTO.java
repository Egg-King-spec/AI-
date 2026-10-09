package org.example.aispingboot.DTO.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ConsultationSessionListItemDTO {

    private Long id;

    private Long userId;

    private String username;

    private String sessionTitle;

    private LocalDateTime startedAt;

    private String lastMessageContent;

    private Long messageCount;

    private Long durationMinutes;

    private String status;
}
