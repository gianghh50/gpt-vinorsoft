package com.vinorsoft.gpt.service.chat.dto;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;

import com.vinorsoft.gpt.service.chat.entity.Conversation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationDto {
	private String conversationId;
	private String username;
	private String title;
	private Date dateCreate;
}
