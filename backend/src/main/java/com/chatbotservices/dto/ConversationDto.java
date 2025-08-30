package com.chatbotservices.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ConversationDto {
	private Long id;
	private List<MessageDTO> messages;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
	private LocalDateTime timestamp;
	private String session_id;
	private String pastSessionId;


	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}


	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	public String getPastSessionId() {
		return pastSessionId;
	}

	public void setPastSessionId(String pastSessionId) {
		this.pastSessionId = pastSessionId;
	}

	public List<MessageDTO> getMessages() {
		return messages;
	}

	public void setMessages(List<MessageDTO> messages) {
		this.messages = messages;
	}
}
