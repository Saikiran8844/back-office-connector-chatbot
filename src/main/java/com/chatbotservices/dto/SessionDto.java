package com.chatbotservices.dto;

import java.time.LocalDateTime;

public class SessionDto {
    private String sessionId;
    private String userName;
    private long userId;
    private LocalDateTime timestamp;
    private LocalDateTime expiryTime;
    private String ipAddress;

    public SessionDto() {}

    public SessionDto(String sessionId, String userName, LocalDateTime timestamp, LocalDateTime expiryTime, String ipAddress, long userId) {
        this.sessionId = sessionId;
        this.userName = userName;
        this.timestamp = timestamp;
        this.expiryTime = expiryTime;
        this.ipAddress = ipAddress;
        this.userId = userId;
    }

    // Getters and Setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
