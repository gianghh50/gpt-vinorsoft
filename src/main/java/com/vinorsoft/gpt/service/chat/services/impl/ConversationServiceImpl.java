package com.vinorsoft.gpt.service.chat.services.impl;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vinorsoft.gpt.service.chat.converter.ConversationConverter;
import com.vinorsoft.gpt.service.chat.custom.Pagination;
import com.vinorsoft.gpt.service.chat.custom.ResponseFormat;
import com.vinorsoft.gpt.service.chat.dto.ConversationDto;
import com.vinorsoft.gpt.service.chat.dto.GPTResponseDto;
import com.vinorsoft.gpt.service.chat.dto.PaginationDto;
import com.vinorsoft.gpt.service.chat.entity.Conversation;
import com.vinorsoft.gpt.service.chat.entity.Message;
import com.vinorsoft.gpt.service.chat.repository.ConversationRepo;
import com.vinorsoft.gpt.service.chat.repository.MessageRepo;
import com.vinorsoft.gpt.service.chat.services.interfaces.ConversationService;

@Service
public class ConversationServiceImpl implements ConversationService {

	private static final Logger logger = LoggerFactory.getLogger(ConversationServiceImpl.class);

	@Autowired
	ConversationRepo conversationRepo;

	@Autowired
	MessageRepo messageRepo;

	@Autowired
	ConversationConverter conversationConverter;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private Pagination pagination;
	
	@Autowired
	ResponseFormat responseFormat;

	@Override
	public ResponseEntity<Object> save(ConversationDto dto) {
		Map<String, Object> response = new HashMap<>();
		try {
			Conversation conversation = conversationConverter.toEntity(dto);
			conversation.setDateCreate(new Date());
			conversation.setStatus(1);
			conversation.setTitle("Hội thoại mới!");
			conversationRepo.save(conversation);

			logger.info("Tạo hội thoại thành công! ");
			response.put("code", HttpServletResponse.SC_OK);
			response.put("data", conversation.getConversationId());
			response.put("message", "Tạo hội thoại thành công!");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.info("Có lỗi xảy ra khi tạo hội thoại mới!");
			response.put("code", HttpServletResponse.SC_OK);
			response.put("data", null);
			response.put("message", "Có lỗi xảy ra khi tạo hội thoại mới!");
			return ResponseEntity.ok(response);
		}
	}

	@Override
	public String updateTitle(String id) {
		try {
			Conversation conversation = conversationRepo.getById(UUID.fromString(id));
			List<Message> messages = messageRepo.findByConversationId(id);
			String all_message = "Title this conversation: ";
			for (Message item : messages) {
				all_message += StringEscapeUtils.escapeJson(item.getContent());
			}

			// Gọi API get title

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			Map<String, Object> requestJson = new HashMap<>();
			requestJson.put("userid", 0);
			requestJson.put("message", all_message);
			HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestJson, headers);

			String url = "http://localhost:3000/v1/send_message";
			ResponseEntity<GPTResponseDto> rp = restTemplate.exchange(url, HttpMethod.POST, request,
					GPTResponseDto.class);

			GPTResponseDto result = rp.getBody();
			conversation.setTitle(result.getReply());
			conversationRepo.save(conversation);

			logger.info("Conversation " + id + " has updated with title: " + result.getReply());
			return "Conversation " + id + " has updated with title: " + result.getReply();

		} catch (Exception e) {
			logger.info("Error when update Conversation title! ");
			return "Error when update Conversation title!";
		}
	}

	@Override
	public PaginationDto getConversationByUsername(String username, Integer page, Integer limit) {
		List<Conversation> conversations = conversationRepo.findByUsername(username);
		List<Conversation> sortedList = conversations.stream()
				.sorted(Comparator.comparing(Conversation::getDateCreate).reversed()).collect(Collectors.toList());

		PaginationDto result = pagination.toPage(sortedList, page, limit);

		return result;
	}

	@Override
	public ResponseEntity<Object> delete(String id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Conversation conversation = conversationRepo.getById(UUID.fromString(id));
			conversation.setStatus(0);
			conversationRepo.save(conversation);
			logger.info("Xóa thành công hội thoại: " + id);
			response.put("code", HttpServletResponse.SC_OK);
			response.put("data", null);
			response.put("message", "Xóa thành công hội thoại: " + id);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.info("Có lỗi xảy ra khi xóa hội thoại: " + id);
			response.put("code", HttpServletResponse.SC_BAD_REQUEST);
			response.put("data", null);
			response.put("message", "ó lỗi xảy ra khi xóa hội thoại: " + id);
			return ResponseEntity.ok(response);
		}
	}

	@Override
	public ResponseEntity<Object> getById(String id) {
		try {
			Conversation conversation = conversationRepo.getById(UUID.fromString(id));
			return ResponseEntity.ok(conversation);
		}catch (Exception e) {
			return responseFormat.response(HttpServletResponse.SC_BAD_GATEWAY, null, "Error get conversation id: " + id);
		}
	}

}
