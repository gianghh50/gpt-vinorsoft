package com.vinorsoft.gpt.service.chat.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vinorsoft.gpt.service.chat.dto.PaginationDto;
import com.vinorsoft.gpt.service.chat.dto.StatisticDto;
import com.vinorsoft.gpt.service.chat.services.interfaces.LoginHistoryService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/")
@SecurityRequirement(name = "bearerAuth")
public class LoginHistoryController {

	@Autowired
	LoginHistoryService loginHistoryService;
	@GetMapping("/histories")
	public ResponseEntity<Map<String, Object>> getHistories(
			@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit) {
		PaginationDto listResult = loginHistoryService.getHistory(page, limit);
		Map<String, Object> response = new HashMap<>();
		response.put("page", page);
		response.put("limit", limit);
		response.put("data", listResult.getData());
		response.put("total", listResult.getTotalElements());
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/histories/user")
	public ResponseEntity<Map<String, Object>> getHistoriesByUser(
			@RequestParam(name = "username", required = true) String username,
			@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit) {
		PaginationDto listResult = loginHistoryService.getHistoryByUser(username, page, limit);
		Map<String, Object> response = new HashMap<>();
		response.put("page", page);
		response.put("limit", limit);
		response.put("data", listResult.getData());
		response.put("total", listResult.getTotalElements());
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/histories/statistic")
	public ResponseEntity<Map<String, Object>> getHistoriesStatistic(
			@RequestParam(name = "month", required = false, defaultValue = "12") Integer month) {
		List<StatisticDto> listResult = loginHistoryService.LoginStatistic(month);
		Map<String, Object> response = new HashMap<>();
		response.put("code", HttpServletResponse.SC_OK);
		response.put("data", listResult);
		return ResponseEntity.ok(response);
	}
}
