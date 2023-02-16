package com.vinorsoft.gpt.service.chat.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vinorsoft.gpt.service.chat.custom.ResponseFormat;
import com.vinorsoft.gpt.service.chat.custom.TokenUtilities;
import com.vinorsoft.gpt.service.chat.dto.AccountInfoDto;
import com.vinorsoft.gpt.service.chat.dto.AccountSignUpDto;
import com.vinorsoft.gpt.service.chat.dto.AccountUpdateInforDto;
import com.vinorsoft.gpt.service.chat.dto.PaginationDto;
import com.vinorsoft.gpt.service.chat.dto.StatisticDto;
import com.vinorsoft.gpt.service.chat.services.interfaces.AccountService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

	@Autowired
	AccountService accountService;

	@Autowired
	TokenUtilities tokenUtilities;

	@Autowired
	ResponseFormat responseFormat;

	@GetMapping("/accounts")
	public ResponseEntity<Map<String, Object>> getAccounts(
			@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit) {
		PaginationDto listResult = accountService.getAccounts(page, limit);
		Map<String, Object> response = new HashMap<>();
		response.put("page", page);
		response.put("limit", limit);
		response.put("data", listResult.getData());
		response.put("total", listResult.getTotalElements());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/account")
	public ResponseEntity<Object> getAccount(@RequestParam(name = "username", required = true) String username,
			HttpServletRequest request) {
		if (!tokenUtilities.IsAuthorizeUser(username, request))
			return responseFormat.unauthorizedResponse(null, "Bạn không có quyền truy cập !");
		return accountService.findAccount(username);
	}

	@PutMapping("/account")
	public ResponseEntity<Object> updateInforAccount(@RequestBody AccountUpdateInforDto dto,
			HttpServletRequest request) {
		if (!tokenUtilities.IsAuthorizeUser(dto.getUsername(), request))
			return responseFormat.unauthorizedResponse(null, "Bạn không có quyền truy cập !");
		return accountService.updateInfo(dto);
	}

	@PutMapping("/account/status")
	public ResponseEntity<Object> updateAccountStatus(@RequestParam(name = "username", required = true) String username,
			@RequestParam(name = "status", required = true) Integer status) {
		return accountService.updateStatus(username, status);
	}
	
	@PutMapping("/account/role")
	public ResponseEntity<Object> updateAccountStatus(@RequestParam(name = "username", required = true) String username,
			@RequestParam(name = "role", required = true) String role) {
		return accountService.updateRole(username, role);
	}
	
	@PostMapping("/account/quick_sign_up")
	public ResponseEntity<Object> signUp(@Valid @RequestBody AccountSignUpDto dto) {
		return accountService.quickSignUp(dto);
	}
	@PutMapping("/account/change_password")
	public ResponseEntity<Object> changePassword(@RequestParam(name = "password", required = true) String password,
			HttpServletRequest request) {
		String username = tokenUtilities.getClaimsProperty(request).get("sub").toString();
		return accountService.updatePassword(username, password);
	}
	
	@GetMapping("/account/statistic")
	public ResponseEntity<Map<String, Object>> getAccountStatistic(
			@RequestParam(name = "month", required = false, defaultValue = "12") Integer month) {
		List<StatisticDto> listResult = accountService.AccountStatistic(month);
		Map<String, Object> response = new HashMap<>();
		response.put("code", HttpServletResponse.SC_OK);
		response.put("data", listResult);
		return ResponseEntity.ok(response);
	}

}
