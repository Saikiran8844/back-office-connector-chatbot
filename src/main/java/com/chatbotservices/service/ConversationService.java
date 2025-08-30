package com.chatbotservices.service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.chatbotservices.dto.ConversationDto;
import com.chatbotservices.dto.MessageDTO;
import com.chatbotservices.model.Conversation;
import com.chatbotservices.model.User;
import com.chatbotservices.model.UserSubscription;
import com.chatbotservices.repository.ConversationRepository;
import com.chatbotservices.repository.SessionRepository;
import com.chatbotservices.repository.UserRepository;
import com.chatbotservices.repository.UserSubscriptionRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * @author Saikiran Nannapanenni
 * 
 */
@Service
public class ConversationService {

	private static final Logger logger = LoggerFactory.getLogger(ConversationService.class);

	@Autowired
	private ConversationRepository conversationRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SessionRepository sessionRepository;

	@Autowired
	private UserSubscriptionRepository userSubscriptionRepository;
	public Long saveFormattedConversation(String sessionId, ConversationDto conversationDto) {
	    Long userId = sessionRepository.findBySessionId(sessionId).getUserId();
	    Optional<UserSubscription> userSubscriptionOpt = userSubscriptionRepository.findByUserId(userId);

	    if (userSubscriptionOpt.isEmpty()) {
	        throw new RuntimeException("Subscription not found for user");
	    }

	    UserSubscription userSubscription = userSubscriptionOpt.get();
	    int used = userSubscription.getConversationsUsed();
	    int limit = userSubscription.getSubscriptionPlan().getConversationLimit();
	    
	    // Free Plan - Enforce Daily Limit
	    if ("Free".equals(userSubscription.getSubscriptionType())) {
	        if (hasExceededDailyLimit(userId, 5)) {
	            throw new RuntimeException("Daily conversation limit reached. Try again tomorrow.");
	        }
	    } 
	    // Paid Plans - Enforce Total Limit
	    else {
	        if (used >= limit) {
	            throw new RuntimeException("Conversation limit reached for your plan. Upgrade to continue.");
	        }
	    }

	    // Save the conversation
	    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
	    String messageJson = new Gson().toJson(conversationDto.getMessages());
	    Conversation conversation = new Conversation(messageJson, conversationDto.getTimestamp(), user, sessionId);
	    conversationRepository.save(conversation);

	    // Update conversation count
	    userSubscription.setConversationsUsed(used + 1);
	    userSubscriptionRepository.save(userSubscription);

	    return conversation.getId();
	}

	public boolean hasExceededDailyLimit(Long userId, int dailyLimit) {
	    LocalDate today = LocalDate.now();

	    // Count the number of conversations the user has today
	    long conversationsToday = conversationRepository.findByUserId(userId)
	        .stream()
	        .filter(conv -> conv.getTimestamp().toLocalDate().isEqual(today))
	        .count();

	    return conversationsToday >= dailyLimit;
	}





	public List<ConversationDto> getConversationsByUserId(String sessionId, String pastSessionId) {
	    Long userId = sessionRepository.findBySessionId(sessionId).getUserId();
	    logger.info("Retrieving conversations for user ID: {}", userId);

	    List<Conversation> conversationData;

	    // Fetch conversations based on session ID or pastSessionId
	    if (!Strings.isBlank(pastSessionId)) {
	        conversationData = conversationRepository.findByPastSessionId(pastSessionId);
	    } else {
	        conversationData = conversationRepository.findByPastSessionId(sessionId);
	    }

	    // Convert to ConversationDto format
	    return conversationData.stream().map(conversation -> {
	        ConversationDto dto = new ConversationDto();
	        dto.setSession_id(conversation.getPastSessionId());
	        dto.setTimestamp(conversation.getTimestamp());
	        
	        // Convert stored JSON messages back to List<MessageDTO>
	        Type listType = new TypeToken<List<MessageDTO>>() {}.getType();
	        List<MessageDTO> messages = new Gson().fromJson(conversation.getMessages(), listType);
	        
	        dto.setMessages(messages);
	        return dto;
	    }).collect(Collectors.toList());
	}


}
