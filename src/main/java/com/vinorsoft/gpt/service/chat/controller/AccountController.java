package com.vinorsoft.gpt.service.chat.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vinorsoft.gpt.service.chat.dto.AccountInfoDto;
import com.vinorsoft.gpt.service.chat.dto.AccountUpdateInforDto;
import com.vinorsoft.gpt.service.chat.dto.PaginationDto;
import com.vinorsoft.gpt.service.chat.services.interfaces.AccountService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

	
	@Autowired
	AccountService accountService;
	
	@GetMapping("/accounts")
	public ResponseEntity<Map<String, Object>> getAccounts(
			@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit){
		PaginationDto listResult = accountService.getAccounts(page, limit);
		Map<String, Object> response = new HashMap<>();
		response.put("page", page);
		response.put("limit", limit);
		response.put("data", listResult.getData());
		response.put("total", listResult.getTotalElements());
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/account")
	public ResponseEntity<Object> getAccount(@RequestParam(name = "username", required = true) String username){
		return accountService.findAccount(username);
	}
	
	@PutMapping("/account")
	public ResponseEntity<Object> updateInforAccount(@RequestBody AccountUpdateInforDto dto){
		return accountService.updateInfo(dto);
	}
	
	@PutMapping("/account/status")
	public ResponseEntity<Object> updateAccountStatus(
			@RequestParam(name = "username", required = true) String username,
			@RequestParam(name = "status", required = true) Integer status){
		return accountService.updateStatus(username, status);
	}
	
	
	
	
}
