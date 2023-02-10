package com.vinorsoft.gpt.service.chat.entity;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account")
public class Account {

	@Id
	@GeneratedValue
	@Column(name="ID")
	private Integer Id;
	@Column(name="USERNAME")
	private String username;
	@Column(name="PASSWORD")
	private String password;
	@Column(name="FIRST_NAME")
	private String firstName;
	@Column(name="LAST_NAME")
	private String lastName;
	@Column(name="SEX")
	private Integer sex;
	@Column(name="DATE_OF_BIRTH")
	private Date dateOfBirth;
	@Column(name="ADDRESS")
	private String address;
	@Column(name="WORKING_POSITION")
	private String workingPosition;
	@Column(name="WORKING_UNIT")
	private String workingUnit;
	@Column(name="PHONE_NUMBER")
	private String phoneNumber;
	@Column(name="EMAIL")
	private String email;
	@Column(name="ROLE")
	private String role;
	@Column(name="IS_ACTIVATED")
	private Integer isActivated;
	@Column(name="OTP")
	private String OTP;
	@Column(name="NOTE")
	private String note;
	@Column(name="DATE_CREATE")
	private Date dateCreate;
	@Column(name="DATE_MODIFY")
	private Date dateModify;
	@Column(name="AVATAR")
	private String avatar;

}
    
