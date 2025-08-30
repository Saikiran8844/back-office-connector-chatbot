package com.chatbotservices.service;

import com.chatbotservices.model.UserSubscription;
import com.chatbotservices.repository.UserSubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author Saikiran Nannapanenni
 * 
 */
@Service
public class ConversationResetService {

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    /**
     * Scheduled task to reset the daily conversation count for free users at midnight
     */
    @Scheduled(cron = "0 0 0 * * ?") // Runs at 12:00 AM daily
    public void resetDailyConversations() {
        List<UserSubscription> freeUsers = userSubscriptionRepository.findAll()
            .stream()
            .filter(sub -> "Free".equals(sub.getSubscriptionType()))
            .collect(Collectors.toList());

        for (UserSubscription sub : freeUsers) {
            sub.setConversationsUsed(0);
            userSubscriptionRepository.save(sub);
        }
        System.out.println("Daily conversation limit reset for Free users.");
    }
}
