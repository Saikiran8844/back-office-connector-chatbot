package com.chatbotservices.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.chatbotservices.dto.ConversationDto;
import com.chatbotservices.dto.MessageContainer;
import com.chatbotservices.dto.MessageDTO;
import com.chatbotservices.dto.UserDto;
import com.chatbotservices.dto.UserSubscriptionDto;
import com.chatbotservices.model.Conversation;
import com.chatbotservices.model.User;
import com.chatbotservices.model.UserSubscription;
import com.google.gson.Gson;

public class EntityUtil {

	public static UserDto convertToDto(User user) {
		UserDto userDto = new UserDto();
		userDto.setId(user.getId());
		userDto.setUserName(user.getUserName());
		userDto.setName(user.getName());
		userDto.setEmail(user.getEmail());
		userDto.setCountry(user.getCountry());
		userDto.setCity(user.getCity());
		userDto.setCourse(user.getCourse());
		userDto.setYear(user.getYear());
		userDto.setCollege(user.getCollege());
		userDto.setSemester(user.getSemester());

		// Handle null subscriptions safely
		List<UserSubscriptionDto> subscriptions = (user.getSubscriptions() != null) ? user.getSubscriptions().stream()
				.map(EntityUtil::convertToSubscriptionDto).collect(Collectors.toList()) : Collections.emptyList();

		userDto.setSubscriptions(subscriptions);
		return userDto;
	}

	// Convert UserSubscription entity to UserSubscriptionDto
	public static UserSubscriptionDto convertToSubscriptionDto(UserSubscription subscription) {
		UserSubscriptionDto dto = new UserSubscriptionDto();
		dto.setId(subscription.getId());
		dto.setSubscriptionType(subscription.getSubscriptionType());
		dto.setStartDate(subscription.getStartDate());
		dto.setEndDate(subscription.getEndDate());
		dto.setStatus(subscription.getStatus());
		dto.setConversationsUsed(subscription.getConversationsUsed());
		dto.setUserId(subscription.getUser().getId());
		dto.setTransactionTime(subscription.getTransctiontime());
		dto.setTransactionId(subscription.getTransctionId());

		return dto;
	}

	public static List<ConversationDto> convertToConversationDto(List<Conversation> conversationData) {
		List<ConversationDto> result = new ArrayList<ConversationDto>();
		for (Conversation con : conversationData) {
			ConversationDto conversation = new ConversationDto();

			conversation.setId(con.getId());
			conversation.setPastSessionId(con.getPastSessionId());
			conversation.setTimestamp(con.getTimestamp());

			conversation.setMessages(parseJsonWithGson(con.getMessages()));
			result.add(conversation);

		}
		return result;
	}

	private static List<MessageDTO> parseJsonWithGson(String jsonString) {
		Gson gson = new Gson();
		MessageContainer container = gson.fromJson(jsonString, MessageContainer.class);
		return container.getMessages();
	}

}
