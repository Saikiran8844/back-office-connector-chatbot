package com.chatbotservices.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chatbotservices.dto.SessionConversationDTO;
import com.chatbotservices.model.Session;
import com.chatbotservices.repository.SessionRepository;

/**
 * 
 * @author Saikiran Nannapanenni
 * 
 */
@Service
public class SessionService {

	@Autowired
	private SessionRepository sessionRepository;

	@Autowired
	public SessionService(SessionRepository sessionRepository) {
		this.sessionRepository = sessionRepository;
	}

	public Session createSession(Long userId, String ipAddress) {
	    Session session = new Session(userId, ipAddress);
	    return sessionRepository.save(session);
	}


	public Boolean validateSession(String sessionId) throws Exception {
	    Session existingSession = sessionRepository.findBySessionId(sessionId);
	    
	    if (existingSession != null) {
	        LocalDateTime currentDateTime = LocalDateTime.now();
	        long minutes = Duration.between(currentDateTime, existingSession.getExpiryTime()).toMinutes();
	        
	        return (minutes > 0); // Returns true if session is still valid
	    } 
	    
	    return false; // Session is invalid or expired
	}


	

    public Boolean inValidateSession(String sessionId) {
        Session existingSession = sessionRepository.findBySessionId(sessionId);

        if (existingSession != null) {
            existingSession.setExpiryTime(LocalDateTime.now()); // Expire the session
            sessionRepository.save(existingSession);
            return true;
        }
        
        return false; // Session does not exist
    }
	
	 public Long getUserIdFromSession(String sessionId) {
	        Session session = sessionRepository.findBySessionId(sessionId);
	        if (session == null) {
	            throw new RuntimeException("Invalid session ID or session expired");
	        }
	        return session.getUserId();
	    }

	    //Get session details after validating sessionId and extracting userId.
	     
	    public List<SessionConversationDTO> getSessionDetails(String sessionId) {
	        Long userId = getUserIdFromSession(sessionId);
	        return sessionRepository.findSessionDetailsByUserId(userId);
	    }

}
