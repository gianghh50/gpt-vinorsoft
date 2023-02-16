package com.vinorsoft.gpt.service.chat.services.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.vinorsoft.gpt.service.chat.entity.ApiKey;
@Service
public interface ApiKeyService {

	ResponseEntity<Object> getKey(Integer Id);
	
	ResponseEntity<Object> getKeys();
	
	ResponseEntity<Object> addKey(String key);
	
	ResponseEntity<Object> deleteKey(Integer id);
	
	ApiKey getRandom();
}
