package com.chatbotservices.controller;

import com.chatbotservices.dto.SessionConversationDTO;
import com.chatbotservices.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * 
 * @author Saikiran Nannapanenni
 * 
 */
@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @Operation(summary = "Get session details with conversation count",
            description = "Fetches session ID, timestamp, and conversation count for the logged-in user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved session details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid session"),
            @ApiResponse(responseCode = "404", description = "No session records found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/userconversationdetails")
    public ResponseEntity<?> getSessionDetails(
            @RequestHeader(name = "sessionId", required = true) String sessionId) {

        try {
            // Validate session before fetching details
            if (!sessionService.validateSession(sessionId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session Expired. Please log in again.");
            }

            List<SessionConversationDTO> sessions = sessionService.getSessionDetails(sessionId);
            if (sessions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No session records found.");
            }
            return ResponseEntity.ok(sessions);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Internal server error: " + e.getMessage());
        }
    }

}
