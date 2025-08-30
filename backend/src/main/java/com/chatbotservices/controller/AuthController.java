package com.chatbotservices.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatbotservices.dto.LoginRequest;
import com.chatbotservices.service.SessionService;
import com.chatbotservices.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * 
 * @author Saikiran Nannapaneni
 *
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private UserService userService;
	@Autowired
	SessionService sessionService;

	@Operation(summary = "Endpoint to login.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Session is Valid"),
			@ApiResponse(responseCode = "401", description = "You are not authorized to access this resource.") })
	@GetMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {

		try {
			String token = userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());

			if (token != null && !token.isBlank()) {
				return ResponseEntity.ok(token);
			} else {
				return ResponseEntity.status(200).body("Invalid username or password");
			}
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("An error occurred during login.:" + e.getMessage());
		}
	}

	@Operation(summary = "Endpoint to check session validity.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Session is Valid"),
			@ApiResponse(responseCode = "401", description = "You are not authorized to access this resource.") })
	@GetMapping("/validate")
	public ResponseEntity<?> validateSession(@RequestHeader(name = "sessionId", required = true) String sessionId) {
		Boolean valid;
		try {
			valid = sessionService.validateSession(sessionId);
			if (valid) {
				return ResponseEntity.status(200).body("Sesion Valid");
			} else {
				return ResponseEntity.status(200).body("Session Expired");
			}
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("An error occurred during login.:" + e.getMessage());
		}

	}

	@Operation(summary = "Endpoint to invalidate the Session.")
	@GetMapping("/logout")
	public ResponseEntity<String> logoutSession(@RequestHeader(name = "sessionId", required = true) String sessionId) {
		boolean isLoggedOut = sessionService.inValidateSession(sessionId);

		if (isLoggedOut) {
			return ResponseEntity.ok("Session invalidated successfully.");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired session.");
		}
	}

}
