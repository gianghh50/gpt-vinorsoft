package com.vinorsoft.gpt.service.chat.services.interfaces;

import java.util.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.vinorsoft.gpt.service.chat.dto.PaginationDto;
import com.vinorsoft.gpt.service.chat.dto.StatisticDto;

@Service
public interface LoginHistoryService {

	PaginationDto getHistory(Integer page, Integer limit);

	PaginationDto getHistoryByUser(String username, Integer page, Integer limit);

	boolean save(String username, String device, String ip, String note, Date time);
	
	List<StatisticDto> LoginStatistic(Integer months);

}
