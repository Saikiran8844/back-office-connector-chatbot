package com.chatbotservices.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "subscriptionplan")
public class SubscriptionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, unique = true, length = 50) // Changed length to 50
    private String name;

    @Column(name = "description", nullable = false, length = 255) // Changed length to 255
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2) // Precision and scale for BigDecimal
    private BigDecimal price;


    @Column(name = "durationInDays", nullable = false)
    private int durationInDays;

    @Column(name = "conversationLimit", nullable = false)
    private int conversationLimit;

    // Constructors
    public SubscriptionPlan() {}

    public SubscriptionPlan(String name, String description, BigDecimal price, int durationInDays, int conversationLimit) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationInDays = durationInDays;
        this.conversationLimit = conversationLimit;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(int durationInDays) {
        this.durationInDays = durationInDays;
    }

    public int getConversationLimit() {
        return conversationLimit;
    }

    public void setConversationLimit(int conversationLimit) {
        this.conversationLimit = conversationLimit;
    }
}

