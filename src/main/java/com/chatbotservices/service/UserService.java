package com.chatbotservices.service;

import java.net.InetAddress;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.chatbotservices.dto.ChangePassword;
import com.chatbotservices.dto.ChangePasswordRequest;
import com.chatbotservices.dto.UserDto;
import com.chatbotservices.dto.UserSubscriptionDto;
import com.chatbotservices.model.Session;
import com.chatbotservices.model.SubscriptionPlan;
import com.chatbotservices.model.User;
import com.chatbotservices.model.UserSubscription;
import com.chatbotservices.repository.SessionRepository;
import com.chatbotservices.repository.SubscriptionPlanRepository;
import com.chatbotservices.repository.UserRepository;
import com.chatbotservices.repository.UserSubscriptionRepository;
import com.chatbotservices.util.EntityUtil;
import com.chatbotservices.util.SymmetricKeyCryptography;
/**
 * 
 * @author Saikiran Nannapanenni
 * 
 */
@Service
public class UserService {

	@Value("${secret.key}")
	private String secretKey;
	@Value("${app.hosted-url}")
	private String hostedUrl;
	@Value("${password.reset.token.expiry}")
	private long expiryTime;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SubscriptionPlanRepository subscriptionPlanRepository;

	@Autowired
	private SessionRepository sessionRepository;

	@Autowired
	private SessionService sessionService;
	@Autowired
	private MailService mailService;

	@Autowired
	private UserSubscriptionRepository userSubscriptionRepository;

	private final BCryptPasswordEncoder passwordEncoder;

	public User registerUser(User user, String subscriptionName) throws Exception {

//		// Validating the password
//		if (!isValidPassword(user.getPassword())) {
//			throw new IllegalArgumentException(
//					"Password must be at least 8 characters long and contain at least one letter, one digit, and one special character.");
//		}

		// Validate if username and email already exist
		if (userRepository.findByUserName(user.getUserName()).isPresent()) {
			throw new Exception("Username already exists");
		}
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new Exception("Email ID already exists");
		}

		// Encrypt the password
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		// Find or create the subscription plan
		SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findByName(subscriptionName)
				.orElseThrow(() -> new Exception("Invalid subscription plan"));

		// Create a UserSubscription record
		UserSubscription userSubscription = new UserSubscription();
		userSubscription.setUser(user);
		userSubscription.setSubscriptionPlan(subscriptionPlan);
		userSubscription.setSubscriptionType(subscriptionPlan.getName());
		userSubscription.setStartDate(LocalDate.now());
		userSubscription.setEndDate(LocalDate.now().plusDays(subscriptionPlan.getDurationInDays()));
		userSubscription.setStatus("Active");
		userSubscription.setConversationsUsed(0);
		userSubscription.setTransctionId("");
		userSubscription.setTransctiontime(LocalDate.now());

		user.setSubscriptions(List.of(userSubscription));

		return userRepository.save(user);
	}

	public String loginUser(String userName, String password) throws Exception {
		Optional<User> optionalUser = userRepository.findByUserName(userName);

		if (optionalUser.isPresent() && passwordEncoder.matches(password, optionalUser.get().getPassword())) {
			String ipAddress = InetAddress.getLocalHost().getHostAddress();

			// Always create a new session instead of updating an existing one
			Session newSession = sessionService.createSession(optionalUser.get().getId(), ipAddress);
			return newSession.getSessionId();
		}

		return ""; // Return empty string if login fails
	}

	public UserDto getUserById(String sessionId) {
		Session exsistingSession = sessionRepository.findBySessionId(sessionId);
		User user = userRepository.findById(exsistingSession.getUserId()).orElse(null);
		if (user == null) {
			return null;
		}
		return EntityUtil.convertToDto(user);
	}

	public UserDto getUserBySessionId(String sessionId) {
		// Fetch session by sessionId
		Session existingSession = sessionRepository.findBySessionId(sessionId);

		// Check if session is null (invalid session ID)
		if (existingSession == null) {
			throw new RuntimeException("Invalid session ID or session expired");
		}

		// Fetch user by userId
		User user = userRepository.findById(existingSession.getUserId()).orElse(null);

		// If user not found, return null response
		if (user == null) {
			throw new RuntimeException("User not found for the given session");
		}

		// Convert user entity to DTO
		return EntityUtil.convertToDto(user);
	}

	public UserDto getUserByEmail(String emailId) {
		User user = userRepository.findByEmail(emailId).orElse(null);
		if (user == null) {
			return null;
		}
		return EntityUtil.convertToDto(user);
	}

	public List<UserSubscriptionDto> getUserSubscriptions(String sessionId) {
		// Fetch session by sessionId
		Session existingSession = sessionRepository.findBySessionId(sessionId);

		// Check if session is null (invalid session ID)
		if (existingSession == null) {
			throw new RuntimeException("Invalid session. Please log in again.");
		}

		// Fetch user by userId
		User user = userRepository.findById(existingSession.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found for the given session"));

		// Retrieve user subscriptions
		return user.getSubscriptions().stream().map(EntityUtil::convertToSubscriptionDto).collect(Collectors.toList());
	}

	public void updateUserDetails(UserDto userDto) {
		User existingUser = userRepository.findById(userDto.getId())
				.orElseThrow(() -> new RuntimeException("User not found"));

		existingUser.setUserName(userDto.getUserName());
		existingUser.setName(userDto.getName());
		existingUser.setEmail(userDto.getEmail());
		existingUser.setCity(userDto.getCity());
		existingUser.setCountry(userDto.getCountry());
		existingUser.setCourse(userDto.getCourse());
		existingUser.setYear(userDto.getYear());
		existingUser.setCollege(userDto.getCollege());
		existingUser.setSemester(userDto.getSemester());

		userRepository.save(existingUser);
	}

	public ResponseEntity<String> updateUserSubscriptionDetails(String sessionId,
			UserSubscriptionDto userSubscriptionDto) {
		try {
			Session existingSession = sessionRepository.findBySessionId(sessionId);

			if (existingSession == null) {
				return ResponseEntity.status(401).body("Session not found or expired");
			}

			User user = userRepository.findById(existingSession.getUserId())
					.orElseThrow(() -> new RuntimeException("User not found"));

			UserSubscription userSubscription = userSubscriptionRepository.findByUserId(user.getId())
					.orElseThrow(() -> new RuntimeException("User subscription not found"));

			userSubscription.setSubscriptionType(userSubscriptionDto.getSubscriptionType());
			userSubscription.setStartDate(userSubscriptionDto.getStartDate());
			userSubscription.setEndDate(userSubscriptionDto.getEndDate());
			userSubscription.setStatus(userSubscriptionDto.getStatus());
			userSubscription.setConversationsUsed(userSubscriptionDto.getConversationsUsed());

			userSubscription.setTransctionId(userSubscriptionDto.getTransactionId());
			userSubscription.setTransctiontime(userSubscriptionDto.getTransactionTime());

			userSubscriptionRepository.save(userSubscription);

			return ResponseEntity.ok("Subscription details updated successfully!");
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Error updating subscription: " + e.getMessage());
		}
	}

	public UserService() {
		this.passwordEncoder = new BCryptPasswordEncoder();
	}

	public ResponseEntity<String> changePassword(ChangePasswordRequest request, String sessionId) {
		// Validate session
		Session existingSession = sessionRepository.findBySessionId(sessionId);
		if (existingSession == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired session.");
		}

		// Retrieve user
		Optional<User> optionalUser = userRepository.findById(existingSession.getUserId());
		if (optionalUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
		}

		User user = optionalUser.get();

		// Verify current password
		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Current password is incorrect.");
		}

		// Validate new passwords
		if (!request.getNewPassword().equals(request.getConfirmPassword())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New passwords do not match.");
		}

		// Hash and update the new password
		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userRepository.save(user);

		return ResponseEntity.ok("Password changed successfully.");

	}

	public ResponseEntity<?> forgotPassword(String emailId) {
		User user = userRepository.findByEmail(emailId).orElse(null);
		if (user == null) {
			return ResponseEntity.status(404).body("User not found");
		}
		String verificationLink = getVerificationLink(emailId);
		try {
			mailService.sendResetPasswordEmail(emailId, verificationLink, user);
			return ResponseEntity.status(200).body("Mail Sent Sucessfully. ");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Email failed to Sent.");
		}

	}

	private String getVerificationLink(String emailId) {

		long timestamp = System.currentTimeMillis();
		String encrypted = new String(
				SymmetricKeyCryptography.encrypt(secretKey, String.format("%s:%d", emailId, timestamp)));
		// URL should follow front
		
//	    String encrypted1 = Base64.getUrlEncoder().encodeToString(encrypted1);

	    return hostedUrl + "?key=" + encrypted; 
	}

	public ResponseEntity<?> validateToken(String token) {
		String decrypted = SymmetricKeyCryptography.decrypt(secretKey, token);
		if (decrypted == null) {
			return ResponseEntity.status(400).body("Invalid token.");
		}

		String[] parts = decrypted.split(":");
		if (parts.length != 2) {
			return ResponseEntity.status(400).body("Malformed token.");
		}

		long timestamp = Long.parseLong(parts[1]);

		if (System.currentTimeMillis() - timestamp > expiryTime) {
			return ResponseEntity.status(400).body("Password reset link has expired.");
		}

		return ResponseEntity.ok("Token is valid. Proceed with password reset.");
	}

	public ResponseEntity<?> resetPassword(ChangePassword request, String token) {
		// Step 1: Validate the token
		String decrypted = SymmetricKeyCryptography.decrypt(secretKey, token);
		if (decrypted == null) {
			return ResponseEntity.status(400).body("Invalid token.");
		}

		String[] parts = decrypted.split(":");
		if (parts.length != 2) {
			return ResponseEntity.status(400).body("Malformed token.");
		}

		long timestamp = Long.parseLong(parts[1]);
		if (System.currentTimeMillis() - timestamp > expiryTime) {
			return ResponseEntity.status(400).body("Password reset link has expired.");
		}

		// Step 2: Get user by email
		String email = parts[0];
		User user = userRepository.findByEmail(email).orElse(null);
		if (user == null) {
			return ResponseEntity.status(404).body("User not found.");
		}

		// Step 3: Validate password strength
		if (!isValidPassword(request.getNewPassword())) {
			return ResponseEntity.status(400)
					.body("Password must be at least 8 characters long and contain letters,numbers and characters.");
		}

		// Step 4: Check if new passwords match
		if (!request.getNewPassword().equals(request.getConfirmPassword())) {
			return ResponseEntity.status(400).body("Passwords do not match.");
		}

		// Step 5: Encrypt & update password
		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userRepository.save(user);

		try {
			mailService.sendPasswordChangedEmail(user.getEmail(), user);
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Password changed, but email notification failed.");
		}

		return ResponseEntity.ok("Password has been reset successfully.");

	}

	private boolean isValidPassword(String password) {
		return password.length() >= 8 && password.matches(".*[A-Za-z].*") && password.matches(".*\\d.*")
				&& password.matches(".*[!@#$%^&*()].*");
	}

}
