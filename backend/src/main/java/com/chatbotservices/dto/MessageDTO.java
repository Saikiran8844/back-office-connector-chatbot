package com.chatbotservices.dto;

public class MessageDTO {
	private String user_message;
	private String actual_response;
	private String suggestion_topic;
	private String suggested_questions;

	public MessageDTO() {
	}

	public MessageDTO(String user_message, String actual_response, String suggestion_topic,
			String suggested_questions) {
		this.user_message = user_message;
		this.actual_response = actual_response;
		this.suggestion_topic = suggestion_topic;
		this.suggested_questions = suggested_questions;
	}

	// Getters and Setters
	public String getUser_message() {
		return user_message;
	}

	public void setUser_message(String user_message) {
		this.user_message = user_message;
	}

	public String getActual_response() {
		return actual_response;
	}

	public void setActual_response(String actual_response) {
		this.actual_response = actual_response;
	}

	public String getSuggestion_topic() {
		return suggestion_topic;
	}

	public void setSuggestion_topic(String suggestion_topic) {
		this.suggestion_topic = suggestion_topic;
	}

	public String getSuggested_questions() {
		return suggested_questions;
	}

	public void setSuggested_questions(String suggested_questions) {
		this.suggested_questions = suggested_questions;
	}
}
