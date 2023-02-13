package com.vinorsoft.gpt.service.chat.services.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vinorsoft.gpt.service.chat.custom.Pagination;
import com.vinorsoft.gpt.service.chat.dto.PaginationDto;
import com.vinorsoft.gpt.service.chat.entity.LoginHistory;
import com.vinorsoft.gpt.service.chat.repository.LoginHistoryRepo;
import com.vinorsoft.gpt.service.chat.services.interfaces.LoginHistoryService;

@Service
public class LoginHistoryServiceImpl implements LoginHistoryService {
	private static final Logger logger = LoggerFactory.getLogger(LoginHistoryServiceImpl.class);

	@Autowired
	LoginHistoryRepo loginHistoryRepo;
	@Autowired
	Pagination pagination;
	
	@Override
	public PaginationDto getHistory(Integer page, Integer limit) {
		List<LoginHistory> listLogin = loginHistoryRepo.findAll();
		return pagination.toPage(listLogin, page, limit);
	}

	@Override
	public PaginationDto getHistoryByUser(String username, Integer page, Integer limit) {
		List<LoginHistory> lisLoginHistories = loginHistoryRepo.getHistoryByUsername(username);
		return pagination.toPage(lisLoginHistories, page, limit);
	}

	@Override
	public boolean save(String username, String device, String ip, String note, Date time) {
		LoginHistory entity = new LoginHistory(null, username,device,ip,note, time);
		try {
			loginHistoryRepo.save(entity);
		}
		catch (Exception e) {
			logger.info("Error save log: " + e.toString());
			return false;
		}
		return true;
	}
	

}
