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
@Table(name = "conversation")
public class Conversation {
	@Id
	@GeneratedValue
	@Type(type = "org.hibernate.type.UUIDCharType")
	@Column(name="CONVERSATION_ID")
	private UUID conversationId;
	@Column(name="USERNAME")
	private String username;
	@Column(name="TITLE")
	private String title;
	@Column(name="DATE_CREATE")
	private Date dateCreate;
	@Column(name="STATUS")
	private Integer status;
}
