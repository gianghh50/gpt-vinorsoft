package com.vinorsoft.gpt.service.chat.converter;

import org.springframework.stereotype.Component;

import com.vinorsoft.gpt.service.chat.dto.MessageDto;
import com.vinorsoft.gpt.service.chat.entity.Message;

@Component
public class MessageConverter {

	public Message toEntity(MessageDto dto) {
		Message entity = new Message();
		entity.setContent(dto.getContent());
		entity.setConversationId(dto.getConversationId());
		entity.setType(dto.getType());
		
		return entity;
	}
	
	public MessageDto toDto(Message entity) {
		MessageDto dto = new MessageDto();
		dto.setContent(entity.getContent());
		dto.setConversationId(entity.getConversationId());
		dto.setType(entity.getType());
		dto.setDateCreate(entity.getDateCreate());
		dto.setMessageId(entity.getMessageId());
		
		return dto;
	}
}
