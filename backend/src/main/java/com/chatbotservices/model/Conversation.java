package com.chatbotservices.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "conversation")
public class Conversation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "messages", nullable = false, columnDefinition = "TEXT")
	private String messages; // Stores user and bot messages in JSON format
	// @ElementCollection
//    @CollectionTable(name = "conversation_messages", joinColumns = @JoinColumn(name = "conversation_id"))
//    private List<Message> messages;

	@Column(name = "timestamp", nullable = false)
	private LocalDateTime timestamp;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "session_id", nullable = false)
	private String pastSessionId;

	public Conversation() {
	}

	public Conversation(String messages, LocalDateTime timestamp, User user, String pastSessionId) {
		this.messages = messages;
		this.timestamp = timestamp;
		this.user = user;
		this.pastSessionId = pastSessionId;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessages() {
		return messages;
	}

	public void setMessages(String messages) {
		this.messages = messages;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPastSessionId() {
		return pastSessionId;
	}

	public void setPastSessionId(String pastSessionId) {
		this.pastSessionId = pastSessionId;
	}
}
