
package com.chatbotservices.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chatbotservices.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserName(String userName);

	Optional<User> findByEmail(String email);

	Optional<User> findByUserNameAndPassword(String userName, String password);

	Optional<User> findById(Long id);

	Optional<User> findUserWithSubscriptionsById(Long userId);

}