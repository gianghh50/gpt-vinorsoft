package com.vinorsoft.gpt.service.chat.custom;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ResponseFormat {

	public ResponseEntity<Object> response(Integer code, Object data, String message){
		Map<String,Object> response = new HashMap<>();
		response.put("code", code);
		response.put("data", data);
		response.put("message", message);
		return ResponseEntity.ok(response); 
	}
}
