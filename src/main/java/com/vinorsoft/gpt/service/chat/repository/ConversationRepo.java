package com.vinorsoft.gpt.service.chat.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vinorsoft.gpt.service.chat.entity.Conversation;

@Repository
public interface ConversationRepo extends JpaRepository <Conversation, UUID>{

	@Query("select a from Conversation a where username = :username and status = 1")
	List<Conversation> findByUsername(@Param("username") String username);
	
}
