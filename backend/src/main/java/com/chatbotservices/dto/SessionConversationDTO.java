package com.chatbotservices.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SessionConversationDTO {
    private String sessionId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")

    private LocalDateTime timestamp;
    private Long conversationCount;

    public SessionConversationDTO(String sessionId, LocalDateTime timestamp, Long conversationCount) {
        this.sessionId = sessionId;
        this.timestamp = timestamp;
        this.conversationCount = conversationCount;
    }

    // Getters and Setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getConversationCount() {
        return conversationCount;
    }

    public void setConversationCount(Long conversationCount) {
        this.conversationCount = conversationCount;
    }
}
