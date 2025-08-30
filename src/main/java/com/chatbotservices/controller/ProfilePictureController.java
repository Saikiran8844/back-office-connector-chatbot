
package com.chatbotservices.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.chatbotservices.service.ProfilePictureService;
import com.chatbotservices.service.SessionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


/**
 * 
 * @author Saikiran Nannapanenni
 * 
 */
@RestController
@RequestMapping("/api/users")
public class ProfilePictureController {

	@Autowired
	private ProfilePictureService profilePictureService;
	@Autowired
	private SessionService sessionService;

	@Operation(summary = "Upload a profile picture", description = "Uploads a profile picture for the authenticated user. Requires a valid session.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Profile picture uploaded successfully"),
			@ApiResponse(responseCode = "401", description = "Unauthorized - Invalid session"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@PostMapping(value = "/uploadProfilePic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadProfilePicture(@RequestHeader(name = "sessionId") String sessionId,
			@RequestParam("file") MultipartFile file) {

		try {
			// Validate session before proceeding
			if (sessionId == null || sessionId.isEmpty() || !sessionService.validateSession(sessionId)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session Expired. Please log in again.");
			}

			String message = profilePictureService.uploadProfilePicture(sessionId, file);
			return ResponseEntity.ok(message);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
		}
	}

	@Operation(summary = "Get profile picture", description = "Retrieves the profile picture of the authenticated user. Requires a valid session.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Profile picture retrieved successfully"),
			@ApiResponse(responseCode = "401", description = "Unauthorized - Invalid session"),
			@ApiResponse(responseCode = "404", description = "Profile picture not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping("/getProfilePic")
	public ResponseEntity<?> getProfilePicture(@RequestHeader(name = "sessionId") String sessionId) {
		try {
			// Validate session before retrieving the image
			if (sessionId == null || sessionId.isEmpty() || !sessionService.validateSession(sessionId)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session Expired. Please log in again.");
			}

			byte[] image = profilePictureService.getProfilePicture(sessionId);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile picture not found for the user.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while retrieving the profile picture.");
		}
	}

	@Operation(summary = "Delete profile picture", description = "Deletes the profile picture of the authenticated user. Requires a valid session.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Profile picture deleted successfully"),
			@ApiResponse(responseCode = "401", description = "Unauthorized - Invalid session"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@DeleteMapping("/deleteProfilePic")
	public ResponseEntity<String> deleteProfilePicture(@RequestHeader(name = "sessionId") String sessionId) {
		try {
			// Validate session before deletion
			if (sessionId == null || sessionId.isEmpty() || !sessionService.validateSession(sessionId)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session Expired. Please log in again.");
			}

			String message = profilePictureService.deleteProfilePicture(sessionId);
			return ResponseEntity.ok(message);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Delete failed: " + e.getMessage());
		}
	}

}
