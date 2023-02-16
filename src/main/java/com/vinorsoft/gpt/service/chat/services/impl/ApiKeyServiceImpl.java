package com.vinorsoft.gpt.service.chat.services.impl;

import java.util.Date;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.vinorsoft.gpt.service.chat.custom.ResponseFormat;
import com.vinorsoft.gpt.service.chat.entity.ApiKey;
import com.vinorsoft.gpt.service.chat.repository.ApiKeyRepo;
import com.vinorsoft.gpt.service.chat.services.interfaces.ApiKeyService;

@Service
public class ApiKeyServiceImpl implements ApiKeyService {

	private static final Logger logger = LoggerFactory.getLogger(ApiKeyServiceImpl.class);
	@Autowired
	ApiKeyRepo apiKeyRepo;

	@Autowired
	ResponseFormat responseFormat;

	@Override
	public ResponseEntity<Object> getKey(Integer Id) {
		try {
			return ResponseEntity.ok(apiKeyRepo.getById(Id));
		} catch (Exception e) {
			logger.error("Error get ApiKey: " + e.toString());
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null, "Error get key");
		}
	}

	@Override
	public ResponseEntity<Object> getKeys() {
		try {
			return ResponseEntity.ok(apiKeyRepo.findAll());
		} catch (Exception e) {
			logger.error("Error get ApiKeys: " + e.toString());
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null, "Error get keys");
		}
	}

	@Override
	public ResponseEntity<Object> addKey(String key) {
		try {
			ApiKey apiKey = new ApiKey(null, key, new Date());
			apiKeyRepo.save(apiKey);
			logger.info("Added ApiKey: " + key);
			return responseFormat.response(HttpServletResponse.SC_OK, null, "Added ApiKey: " + key);
		} catch (Exception e) {
			logger.error("Error add ApiKey: " + e.toString());
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null, "Error add key");
		}
	}

	@Override
	public ResponseEntity<Object> deleteKey(Integer id) {
		try {
			ApiKey apiKey = apiKeyRepo.getById(id);
			apiKeyRepo.delete(apiKey);
			logger.info("Deleted ApiKey: " + apiKey.getKey());
			return responseFormat.response(HttpServletResponse.SC_OK, null, "Deleted ApiKey: " + apiKey.getKey());
		} catch (Exception e) {
			logger.error("Error delete ApiKey: " + e.toString());
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null, "Error delete apiKey");
		}
	}

	@Override
	public ApiKey getRandom() {
		Random random = new Random();
		try {
			Integer id = random.nextInt(3) + 1;
			return apiKeyRepo.getById(id);
		} catch (Exception e) {
		}
		return null;
	}

}
