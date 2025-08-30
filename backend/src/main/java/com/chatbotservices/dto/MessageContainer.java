package com.chatbotservices.dto;

import java.util.List;

public class MessageContainer {
    private List<MessageDTO> messages;
    
    public List<MessageDTO> getMessages() {
        return messages;
    }
    
    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }
}