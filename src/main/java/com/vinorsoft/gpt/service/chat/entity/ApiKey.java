package com.vinorsoft.gpt.service.chat.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "api_key")
public class ApiKey {

	@Id
	@GeneratedValue
	@Column(name="KEY_ID")
	private Integer keyId;
	@Column(name="KEY")
	private String key;
	@Column(name="DATE_CREATE")
	private Date dateCreate;
}
