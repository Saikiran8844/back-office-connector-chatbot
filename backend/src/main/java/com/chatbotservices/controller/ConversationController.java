package com.chatbotservices.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chatbotservices.dto.ConversationDto;
import com.chatbotservices.service.ConversationService;
import com.chatbotservices.service.SessionService;
import com.rollbar.notifier.Rollbar;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * 
 * @author Saikiran Nannapanenni
 * 
 */
@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
	private static final Logger logger = LoggerFactory.getLogger(ConversationController.class);
	private Rollbar rollbar;


	@Autowired
	private ConversationService conversationService;
	@Autowired
	private SessionService sessionService;

	@Operation(summary = "Save a conversation", description = "Saves a new conversation for the user session.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Conversation saved successfully."),
			@ApiResponse(responseCode = "400", description = "Invalid request. Messages cannot be empty."),
			@ApiResponse(responseCode = "401", description = "Unauthorized. Invalid session."),
			@ApiResponse(responseCode = "500", description = "Internal server error.") })
	@PostMapping("/saveConversation")
	public ResponseEntity<?> saveConversations(@RequestHeader(name = "sessionId", required = true) String sessionId,
			@RequestBody ConversationDto conversationDto) {

		try {
			if (!sessionService.validateSession(sessionId)) {
				return ResponseEntity.status(401).body("Invalid session. Please log in again.");
			}

			if (conversationDto.getMessages() == null || conversationDto.getMessages().isEmpty()) {
				return ResponseEntity.badRequest().body("Messages cannot be empty.");
			}

			logger.info("Saving conversation for session: {}", sessionId);

			// Save conversation
			Long conversationId = conversationService.saveFormattedConversation(sessionId, conversationDto);

			return ResponseEntity.ok("Conversation saved successfully with ID: " + conversationId);
		} catch (Exception e) {
			logger.error("Failed to save conversation: {}", e.getMessage());
			return ResponseEntity.internalServerError().body("Failed to save conversation: " + e.getMessage());
		}
	}

//	@PostMapping("/saveConversation")
//	public ResponseEntity<?> saveConversations(@RequestHeader(name = "sessionId", required = true) String sessionId,
//			@RequestBody ConversationDto conversationDto) {
//		try {
//			Long conversationId = conversationService.saveFormattedConversation(sessionId, conversationDto);
//			return ResponseEntity.ok("Conversation saved successfully with ID: " + conversationId);
//		} catch (RuntimeException e) {
//			return ResponseEntity.status(429).body(e.getMessage()); // Return HTTP 429 for "Too Many Requests"
//		} catch (Exception e) {
//			logger.error("Failed to save conversation: {}", e.getMessage());
//			return ResponseEntity.internalServerError().body("Error saving conversation: " + e.getMessage());
//		}
//	}

	@Operation(summary = "Get user conversations", description = "Fetches the list of conversations for a given session.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieved conversations."),
			@ApiResponse(responseCode = "401", description = "Unauthorized. Invalid session."),
			@ApiResponse(responseCode = "500", description = "Internal server error.") })
	@GetMapping("/getConversations")
	public ResponseEntity<?> getUserConversations(@RequestHeader(name = "sessionId", required = true) String sessionId,
			@RequestParam(name = "pastSessionId", required = false) String pastSessionId) {

		try {
			// Validate session
			if (!sessionService.validateSession(sessionId)) {
				return ResponseEntity.status(401).body("Invalid session. Please log in again.");
			}

			logger.info("Fetching conversations for user: {}", sessionId);

			// Fetch conversations
			List<ConversationDto> conversations = conversationService.getConversationsByUserId(sessionId,
					pastSessionId);

			if (conversations.isEmpty()) {
				return ResponseEntity.ok("No conversations found for user " + sessionId);
			}

			return ResponseEntity.ok(conversations);
		} catch (Exception e) {
			logger.error("Error fetching conversations for Session {}: {}", sessionId, e.getMessage(), e);
			return ResponseEntity.internalServerError().body("Error fetching conversations: " + e.getMessage());
		}
	}

}
