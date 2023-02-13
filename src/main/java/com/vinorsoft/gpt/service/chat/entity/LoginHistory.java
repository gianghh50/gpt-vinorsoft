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
@Table(name = "login_history")
public class LoginHistory {

	@Id
	@GeneratedValue
	@Type(type = "org.hibernate.type.UUIDCharType")
	@Column(name="LOG_ID")
	private UUID logId;
	@Column(name="USERNAME")
	private String username;
	@Column(name="DEVICE")
	private String device;
	@Column(name="IP_ADDRESS")
	private String ipAddress;
	@Column(name="NOTE")
	private String note;
	@Column(name="TIME")
	private Date time;
}
