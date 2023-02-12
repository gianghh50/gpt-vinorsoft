package com.vinorsoft.gpt.service.chat.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.vinorsoft.gpt.service.chat.dto.ConversationDto;
import com.vinorsoft.gpt.service.chat.dto.MessageDto;
import com.vinorsoft.gpt.service.chat.dto.PaginationDto;
import com.vinorsoft.gpt.service.chat.services.interfaces.ConversationService;
import com.vinorsoft.gpt.service.chat.services.interfaces.MessageService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin("http://localhost:3000")
public class MessageController {

	@Autowired
	MessageService messageService;
	
	@GetMapping("/message")
	public ResponseEntity<Map<String, Object>> getMessage(
			@RequestParam(name = "id", required = true) String id,
			@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit){
		PaginationDto listResult = messageService.getPageMessageByConversationId(id, page, limit);
		Map<String, Object> response = new HashMap<>();
		response.put("page", page);
		response.put("limit", limit);
		response.put("data", listResult.getData());
		response.put("total", listResult.getTotalElements());
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/message")
	public ResponseEntity<Object> createConversation(@RequestBody MessageDto model){
		return messageService.save(model);
	}

}
