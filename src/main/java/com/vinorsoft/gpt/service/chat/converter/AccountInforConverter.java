package com.vinorsoft.gpt.service.chat.converter;

import org.springframework.stereotype.Component;

import com.vinorsoft.gpt.service.chat.dto.AccountDto;
import com.vinorsoft.gpt.service.chat.dto.AccountInfoDto;
import com.vinorsoft.gpt.service.chat.dto.AccountUpdateInforDto;
import com.vinorsoft.gpt.service.chat.entity.Account;

@Component
public class AccountInforConverter {
	public Account toEntity(AccountInfoDto dto) {
		Account entity = new Account();
		entity.setId(dto.getId());
		entity.setUsername(dto.getUsername());
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setSex(dto.getSex());
		entity.setDateOfBirth(dto.getDateOfBirth());
		entity.setAddress(dto.getAddress());
		entity.setWorkingPosition(dto.getWorkingPosition());
		entity.setWorkingUnit(dto.getWorkingUnit());
		entity.setPhoneNumber(dto.getPhoneNumber());
		entity.setEmail(dto.getEmail());
		entity.setRole(dto.getRole());
		entity.setIsActivated(dto.getIsActivated());
		entity.setNote(dto.getNote());
		entity.setAvatar(dto.getAvatar());
		
		return entity;
	}
	
	public Account toSaveEntity(Account entity, AccountUpdateInforDto dto) {
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setSex(dto.getSex());
		entity.setDateOfBirth(dto.getDateOfBirth());
		entity.setAddress(dto.getAddress());
		entity.setWorkingPosition(dto.getWorkingPosition());
		entity.setWorkingUnit(dto.getWorkingUnit());
		entity.setPhoneNumber(dto.getPhoneNumber());
		
		return entity;
	}
	
	public AccountInfoDto toDto(Account entity) {
		AccountInfoDto dto = new AccountInfoDto();
		dto.setId(entity.getId());
		dto.setUsername(entity.getUsername());
		dto.setFirstName(entity.getFirstName());
		dto.setLastName(entity.getLastName());
		dto.setSex(entity.getSex());
		dto.setDateOfBirth(entity.getDateOfBirth());
		dto.setAddress(entity.getAddress());
		dto.setWorkingPosition(entity.getWorkingPosition());
		dto.setWorkingUnit(entity.getWorkingUnit());
		dto.setPhoneNumber(entity.getPhoneNumber());
		dto.setEmail(entity.getEmail());
		dto.setRole(entity.getRole());
		dto.setIsActivated(entity.getIsActivated());
		dto.setNote(entity.getNote());
		dto.setDateCreate(entity.getDateCreate());
		dto.setDateModify(entity.getDateModify());
		dto.setAvatar(entity.getAvatar());
		return dto;
	}
}
