package com.vinorsoft.gpt.service.chat.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vinorsoft.gpt.service.chat.entity.Message;

@Repository
public interface MessageRepo extends JpaRepository<Message, UUID>{

	@Query("select a from Message a where conversationId = :Id")
	List<Message> findByConversationId(@Param("Id") String conversationId);
}
