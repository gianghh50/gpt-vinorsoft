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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vinorsoft.gpt.service.chat.dto.PaginationDto;
import com.vinorsoft.gpt.service.chat.entity.AcceptedMail;
import com.vinorsoft.gpt.service.chat.services.interfaces.AcceptedMailService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "http://service4all.vinorsoft.com/")
public class MailController {

	@Autowired
	AcceptedMailService acceptedMailService;
	
	@GetMapping("/emails")
	public ResponseEntity<Map<String, Object>> getMails(
			@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit) {
		PaginationDto listResult = acceptedMailService.listMails(page, limit);
		Map<String, Object> response = new HashMap<>();
		response.put("page", page);
		response.put("limit", limit);
		response.put("data", listResult.getData());
		response.put("total", listResult.getTotalElements());
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/email")
	public ResponseEntity<Object> addEmail(@RequestBody AcceptedMail model) {
		return acceptedMailService.addMail(model);
	}

	@DeleteMapping("/email/{id}")
	public ResponseEntity<Object> deleteEmail(@PathVariable("id") Integer id) {
		return acceptedMailService.deleteMail(id);
	}
	
}
