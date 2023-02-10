package com.vinorsoft.gpt.service.chat.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "message")
public class Message {

	@Id
	@GeneratedValue
	@Type(type = "org.hibernate.type.UUIDCharType")
	@Column(name="MESSAGE_ID")
	private UUID messageId;
	@Column(name="CONTENT")
	private String content;
	@Column(name="CONVERSATION_ID")
	private String conversationId;
	@Column(name="DATE_CREATE")
	private Date dateCreate;
	@Column(name="TYPE")
	private Integer type;
}
