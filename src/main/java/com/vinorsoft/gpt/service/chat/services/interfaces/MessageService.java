package com.vinorsoft.gpt.service.chat.services.interfaces;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.vinorsoft.gpt.service.chat.dto.MessageDto;
import com.vinorsoft.gpt.service.chat.dto.PaginationDto;
import com.vinorsoft.gpt.service.chat.entity.Message;

@Service
public interface MessageService {

	ResponseEntity<Object> save(MessageDto dto);
	
	List<Message> getMessageByConversationId(String id);
	
	PaginationDto getPageMessageByConversationId(String id, Integer page, Integer limit);

}
