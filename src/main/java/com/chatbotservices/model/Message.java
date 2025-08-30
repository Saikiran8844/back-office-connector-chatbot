package com.chatbotservices.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    @JsonBackReference
    private Conversation conversation;

    @Column(name = "user", nullable = false)
    private String user;

    @Column(name = "bot", nullable = false)
    private String bot;

    public Message() {}

    public Message(Conversation conversation, String user, String bot) {
        this.conversation = conversation;
        this.user = user;
        this.bot = bot;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Conversation getConversation() { return conversation; }
    public void setConversation(Conversation conversation) { this.conversation = conversation; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getBot() { return bot; }
    public void setBot(String bot) { this.bot = bot; }
}
