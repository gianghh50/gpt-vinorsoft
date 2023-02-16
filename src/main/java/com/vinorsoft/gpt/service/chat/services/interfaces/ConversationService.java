package com.vinorsoft.gpt.service.chat.services.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.vinorsoft.gpt.service.chat.dto.ConversationDto;
import com.vinorsoft.gpt.service.chat.dto.PaginationDto;

@Service
public interface ConversationService {

	ResponseEntity<Object> save(ConversationDto dto);
	
	String updateTitle(String UUID);
	
	PaginationDto getConversationByUsername(String username, Integer page, Integer limit);
	
	ResponseEntity<Object> delete(String id);
	
	ResponseEntity<Object> getById(String id);
	
	Integer deleteBlankConversation(String username);
}
