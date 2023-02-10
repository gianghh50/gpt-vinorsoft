package com.vinorsoft.gpt.service.chat.dto;

import org.springframework.stereotype.Component;

import com.vinorsoft.gpt.service.chat.entity.Account;


@Component
public class AccountConverter {

	public AccountDto toDto(Account entity) {
		AccountDto dto = new AccountDto();
		dto.setId(entity.getId());
		dto.setUsername(entity.getUsername());
		return dto;
	}
}
