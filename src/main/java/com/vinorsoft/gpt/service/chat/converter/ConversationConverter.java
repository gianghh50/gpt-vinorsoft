package com.vinorsoft.gpt.service.chat.converter;

import org.springframework.stereotype.Component;

import com.vinorsoft.gpt.service.chat.dto.ConversationDto;
import com.vinorsoft.gpt.service.chat.entity.Conversation;

@Component
public class ConversationConverter {

	public Conversation toEntity(ConversationDto dto) {
		Conversation entity = new Conversation();
		entity.setUsername(dto.getUsername());
		return entity;
	}
	
	public ConversationDto toDto(Conversation entity) {
		ConversationDto dto = new ConversationDto();
		dto.setConversationId(entity.getConversationId().toString());
		dto.setTitle(entity.getTitle());
		dto.setDateCreate(entity.getDateCreate());
		dto.setUsername(entity.getUsername());
		return dto;
	}
}
