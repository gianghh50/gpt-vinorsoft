package com.vinorsoft.gpt.service.chat.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vinorsoft.gpt.service.chat.custom.Pagination;
import com.vinorsoft.gpt.service.chat.dto.PaginationDto;
import com.vinorsoft.gpt.service.chat.dto.StatisticDto;
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
		List<LoginHistory> listLogin = loginHistoryRepo.getHistory();
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

	@SuppressWarnings("deprecation")
	@Override
	public List<StatisticDto> LoginStatistic(Integer months) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -months);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date start_date = calendar.getTime();
		List<StatisticDto> result = new ArrayList<>();
		for(Integer i = months - 1; i >= 0; i--) {
			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, - i);
			result.add(new StatisticDto(calendar.getTime(), 0));
		}
		List<LoginHistory> loginHistories = loginHistoryRepo.getHistory(start_date, new Date());
		for(LoginHistory loginHistory:loginHistories) {
			for(StatisticDto item: result) {
				if(item.getDate().getMonth() == loginHistory.getTime().getMonth() && item.getDate().getYear() == loginHistory.getTime().getYear()) {
					item.setCount(item.getCount() + 1);
					break;
				}
			}
		}
		
		
		return result;
	}
	

}
