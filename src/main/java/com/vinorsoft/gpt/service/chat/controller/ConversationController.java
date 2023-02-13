package com.vinorsoft.gpt.service.chat.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vinorsoft.gpt.service.chat.custom.ResponseFormat;
import com.vinorsoft.gpt.service.chat.custom.TokenUtilities;
import com.vinorsoft.gpt.service.chat.dto.ConversationDto;
import com.vinorsoft.gpt.service.chat.dto.PaginationDto;
import com.vinorsoft.gpt.service.chat.services.interfaces.ConversationService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin("http://localhost:3000")
public class ConversationController {

	@Autowired
	ConversationService conversationService;

	@Autowired
	TokenUtilities tokenUtilities;

	@Autowired
	ResponseFormat responseFormat;

	@GetMapping("/conversation")
	public ResponseEntity<Map<String, Object>> getConversation(
			@RequestParam(name = "username", required = true) String username,
			@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
			HttpServletRequest request) {

		Map<String, Object> response = new HashMap<>();

		if (!tokenUtilities.IsAuthorizeUser(username, request)) {
			response.put("code", HttpServletResponse.SC_UNAUTHORIZED);
			response.put("data", null);
			response.put("message", "Bạn không có quyền truy cập!");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}

		PaginationDto listResult = conversationService.getConversationByUsername(username, page, limit);
		response.put("page", page);
		response.put("limit", limit);
		response.put("data", listResult.getData());
		response.put("total", listResult.getTotalElements());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/conversation/{id}")
	public ResponseEntity<Object> getConversation(@PathVariable("id") String id) {
		return conversationService.getById(id);
	}

	@PostMapping("/conversation")
	public ResponseEntity<Object> createConversation(@RequestBody ConversationDto model) {
		return conversationService.save(model);
	}

	@DeleteMapping("/conversation/{id}")
	public ResponseEntity<Object> deleteConversation(@PathVariable("id") String id) {
		return conversationService.delete(id);
	}
}
