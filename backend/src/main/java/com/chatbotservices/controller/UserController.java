
package com.chatbotservices.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chatbotservices.dto.ChangePassword;
import com.chatbotservices.dto.ChangePasswordRequest;
import com.chatbotservices.dto.RegisterRequest;
import com.chatbotservices.dto.UserDto;
import com.chatbotservices.dto.UserSubscriptionDto;
import com.chatbotservices.model.User;
import com.chatbotservices.service.SessionService;
import com.chatbotservices.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

/**
 * 
 * @author Saikiran Nannapaneni
 *
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private SessionService sessionService;

	@Operation(summary = "Register a new user", description = "Registers a new user with their details and subscription.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "User registered successfully."),
			@ApiResponse(responseCode = "500", description = "Internal server error.") })

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
		try {
			User user = new User();
			user.setUserName(registerRequest.getUserName());
			user.setPassword(registerRequest.getPassword());
			user.setName(registerRequest.getName());
			user.setEmail(registerRequest.getEmail());
			user.setCity(registerRequest.getCity());
			user.setCountry(registerRequest.getCountry());
			user.setCourse(registerRequest.getCourse());
			user.setYear(registerRequest.getYear());
			user.setCollege(registerRequest.getCollege());
			user.setSemester(registerRequest.getSemester());

			User registeredUser = userService.registerUser(user, registerRequest.getSubscriptionName());
			return ResponseEntity.ok("User registered successfully with ID: " + registeredUser.getId());
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}

	@Operation(summary = "Get user subscription details", description = "Fetches all subscriptions for the logged-in user.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieved subscriptions."),
			@ApiResponse(responseCode = "401", description = "Unauthorized. Invalid session."),
			@ApiResponse(responseCode = "404", description = "No subscriptions found."),
			@ApiResponse(responseCode = "500", description = "Internal server error.") })

	@GetMapping("/subscriptions")
	public ResponseEntity<?> getUserSubscriptionDetails(
			@RequestHeader(name = "sessionId", required = true) String sessionId) {
		try {
			Boolean valid = sessionService.validateSession(sessionId);
			if (valid) {
				List<UserSubscriptionDto> subscriptions = userService.getUserSubscriptions(sessionId);

				if (subscriptions.isEmpty()) {
					return ResponseEntity.status(404).body("No subscriptions found for this user");
				}

				return ResponseEntity.ok(subscriptions);
			} else {
				return ResponseEntity.status(401).body("Session Expired");
			}

		} catch (RuntimeException e) {
			return ResponseEntity.status(401).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.internalServerError()
					.body("Error retrieving subscription details: " + e.getMessage());
		}
	}

	@Operation(summary = "Update user subscription details", description = "Updates the user's subscription information.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Subscription updated successfully."),
			@ApiResponse(responseCode = "401", description = "Unauthorized. Invalid session."),
			@ApiResponse(responseCode = "500", description = "Internal server error.") })

	@PostMapping("/subscriptions")
	public ResponseEntity<?> updateUserSubscriptionDetails(
			@RequestHeader(name = "sessionId", required = true) String sessionId,
			@RequestBody(required = false) UserSubscriptionDto userSubscriptionDto) {
		try {
			Boolean valid = sessionService.validateSession(sessionId);
			if (valid) {
				userService.updateUserSubscriptionDetails(sessionId, userSubscriptionDto);
				return ResponseEntity.status(200).body("updated successfully");
			} else {
				return ResponseEntity.status(401).body("Session Expired");
			}
		} catch (RuntimeException e) {
			return ResponseEntity.status(401).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.internalServerError()
					.body("Error retrieving subscription details: " + e.getMessage());
		}
	}

	@Operation(summary = "Get user details", description = "Fetches the details of the logged-in user.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieved user details."),
			@ApiResponse(responseCode = "401", description = "Unauthorized. Invalid session."),
			@ApiResponse(responseCode = "404", description = "User not found."),
			@ApiResponse(responseCode = "500", description = "Internal server error.") })

	@GetMapping("/user")
	public ResponseEntity<?> getUserDetails(@RequestHeader(name = "sessionId", required = true) String sessionId) {
		try {
			Boolean valid = sessionService.validateSession(sessionId);
			if (valid) {
				UserDto userDto = userService.getUserBySessionId(sessionId);

				if (userDto == null) {
					return ResponseEntity.status(404).body("User not found");
				}
				return ResponseEntity.ok(userDto);
			} else {
				return ResponseEntity.status(401).body("Session Expired");
			}

		} catch (RuntimeException e) {
			return ResponseEntity.status(401).body(e.getMessage()); // Return 401 for invalid session
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Error retrieving user details: " + e.getMessage());
		}
	}

	@Operation(summary = "Update user details", description = "Updates the details of the logged-in user.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "User updated successfully."),
			@ApiResponse(responseCode = "401", description = "Unauthorized. Invalid session."),
			@ApiResponse(responseCode = "500", description = "Internal server error.") })
	@PostMapping("/user")
	public ResponseEntity<?> updateUserDetails(@RequestHeader(name = "sessionId", required = true) String sessionId,
			@RequestBody UserDto userDto) {

		try {
			// Validate session
			Boolean valid = sessionService.validateSession(sessionId);
			if (!valid) {
				return ResponseEntity.status(401).body("Invalid session. Please log in again.");
			}

			// Call service to update details
			userService.updateUserDetails(userDto);

			return ResponseEntity.ok("User updated successfully");
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Error occurred while updating user details");
		}
	}

	@Operation(summary = "Change user password", description = "Allows a logged-in user to change their password.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Password changed successfully."),
			@ApiResponse(responseCode = "401", description = "Unauthorized. Invalid session."),
			@ApiResponse(responseCode = "500", description = "Internal server error.") })
	@PostMapping("/change-password")
	public ResponseEntity<?> changePassword(@RequestHeader(name = "sessionId", required = true) String sessionId,
			@RequestBody ChangePasswordRequest request) {
		return userService.changePassword(request, sessionId);
	}

	@Operation(summary = "Request password reset", description = "Sends a password reset link to the user's registered email.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Password reset link sent successfully"),
			@ApiResponse(responseCode = "404", description = "User not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping("/forgotPassword")
	public ResponseEntity<?> forgotPassword(@RequestParam(name = "email") String emailId) {

		return userService.forgotPassword(emailId);
	}

	@Operation(summary = "Validate password reset token", description = "Checks if the provided password reset token is valid or expired.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Valid token"),
			@ApiResponse(responseCode = "400", description = "Invalid or expired token"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping("/reset")
	public ResponseEntity<?> resetPassword(@RequestParam String token) {
		return userService.validateToken(token);

	}

	@Operation(summary = "Reset user password", description = "Resets the user's password using a valid reset token.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Password reset successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid or expired token"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@PostMapping("/reset")
	public ResponseEntity<?> resetPassword(@RequestParam String token, @Valid @RequestBody ChangePassword request) {

		return userService.resetPassword(request, token);
	}

}