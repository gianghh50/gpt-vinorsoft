package com.vinorsoft.gpt.service.chat.dto;

import java.lang.reflect.Field;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountUpdateInforDto {
	private String username;
	private String firstName;
	private String lastName;
	private Integer sex;
	private Date dateOfBirth;
	private String address;
	private String workingPosition;
	private String workingUnit;
	private String phoneNumber;
	
	public boolean hasBlankField() throws IllegalAccessException {
		for (Field f : getClass().getDeclaredFields()) {
			System.out.println(f.get(this).toString());
			if (f.get(this) == null || f.get(this) == "")
				return true;
		}
		return false;
	}
}
