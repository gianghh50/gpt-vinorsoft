package com.vinorsoft.gpt.service.chat.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfoDto {

	private Integer Id;
	private String username;
	private String firstName;
	private String lastName;
	private Integer sex;
	private Date dateOfBirth;
	private String address;
	private String workingPosition;
	private String workingUnit;
	private String phoneNumber;
	private String email;
	private String role;
	private Integer isActivated;
	private String note;
	private Date dateCreate;
	private Date dateModify;
	private String avatar;

}
