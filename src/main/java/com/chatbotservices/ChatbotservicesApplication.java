package com.chatbotservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.chatbotservices")
public class ChatbotservicesApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatbotservicesApplication.class, args);
    }
}