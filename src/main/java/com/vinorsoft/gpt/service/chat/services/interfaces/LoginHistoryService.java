package com.vinorsoft.gpt.service.chat.services.interfaces;

import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.vinorsoft.gpt.service.chat.dto.PaginationDto;

@Service
public interface LoginHistoryService {

	PaginationDto getHistory(Integer page, Integer limit);
	PaginationDto getHistoryByUser(String username, Integer page, Integer limit);
	boolean save(String username, String device, String ip, String note, Date time);
	
}
