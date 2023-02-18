package com.vinorsoft.gpt.service.chat.services.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.vinorsoft.gpt.service.chat.dto.PaginationDto;
import com.vinorsoft.gpt.service.chat.entity.AcceptedMail;

@Service
public interface AcceptedMailService {

	ResponseEntity<Object> addMail(AcceptedMail mail);
	
	ResponseEntity<Object> deleteMail(Integer id);
	
	PaginationDto listMails(Integer page, Integer limit);
}
