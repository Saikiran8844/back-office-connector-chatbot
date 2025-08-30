package com.chatbotservices.repository;

import com.chatbotservices.dto.SessionConversationDTO;
import com.chatbotservices.model.Session;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SessionRepository extends JpaRepository<Session, Long> {

//	Session findBySessionId(String sessionId);
	
	Session findBySessionIdAndIpAddress(String sessionId, String ipAddress);

	Session findByUserIdAndIpAddress(Long userId, String ipAddress);
	Session findBySessionId(String sessionId);

    @Query("SELECT new com.chatbotservices.dto.SessionConversationDTO(s.sessionId, s.timestamp, COUNT(c.id)) " +
           "FROM Session s LEFT JOIN Conversation c ON s.sessionId = c.pastSessionId " +
           "WHERE s.userId = :userId " +
           "GROUP BY s.sessionId, s.timestamp " +
           "ORDER BY s.timestamp DESC")
    List<SessionConversationDTO> findSessionDetailsByUserId(@Param("userId") Long userId);
}
