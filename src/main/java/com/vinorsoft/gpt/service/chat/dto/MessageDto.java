package com.vinorsoft.gpt.service.chat.dto;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {

	private UUID messageId;
	private String content;
	private String conversationId;
	private Date dateCreate;
	private Integer type;
}
