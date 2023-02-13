package com.vinorsoft.gpt.service.chat.services.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

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

import com.vinorsoft.gpt.service.chat.converter.MessageConverter;
import com.vinorsoft.gpt.service.chat.custom.Pagination;
import com.vinorsoft.gpt.service.chat.dto.GPTResponseDto;
import com.vinorsoft.gpt.service.chat.dto.MessageDto;
import com.vinorsoft.gpt.service.chat.dto.PaginationDto;
import com.vinorsoft.gpt.service.chat.entity.Message;
import com.vinorsoft.gpt.service.chat.repository.MessageRepo;
import com.vinorsoft.gpt.service.chat.services.interfaces.ConversationService;
import com.vinorsoft.gpt.service.chat.services.interfaces.MessageService;

@Service
public class MessageServiceImpl implements MessageService {

	private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
	@Autowired
	MessageRepo messageRepo;

	@Autowired
	private Pagination pagination;

	@Autowired
	MessageConverter messageConverter;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ConversationService conversationService;

	@Override
	public ResponseEntity<Object> save(MessageDto dto) {

		Map<String, Object> response = new HashMap<>();

		// Lấy reply từ GPT
		GPTResponseDto result;

		// Lưu message người dùng gửi lên
		Message message;
		try {
			message = messageConverter.toEntity(dto);
			message.setDateCreate(new Date());
			messageRepo.save(message);
		} catch (Exception e) {
			logger.info("Lỗi khi lưu tin nhắn user! ");
			response.put("code", HttpServletResponse.SC_BAD_REQUEST);
			response.put("data", null);
			response.put("message", "Lỗi khi lưu tin nhắn user! ");
			return ResponseEntity.ok(response);
		}
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			Map<String, Object> requestJson = new HashMap<>();
			requestJson.put("userid", 1);
			requestJson.put("message", dto.getContent());
			HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestJson, headers);

			String url = "http://localhost:3000/v1/send_message";
			ResponseEntity<GPTResponseDto> rp = restTemplate.exchange(url, HttpMethod.POST, request,
					GPTResponseDto.class);

			result = rp.getBody();
//			String reply = result.getReply().replaceAll("^\\n|\\n$", "").replaceAll("\n", "<br>");
			String reply = result.getReply().replaceAll("^\\n|\\n$", "");
			result.setReply(reply);
		} catch (Exception e) {
			logger.info("Lỗi request đến GPT API! : " + e.toString());
			response.put("code", HttpServletResponse.SC_BAD_REQUEST);
			response.put("data", "Error getting response!");
			response.put("message", "Lỗi request đến GPT API! ");
			messageRepo.delete(message);
			return ResponseEntity.ok(response);
		}
		// Lưu tin nhắn GPT
		Message messageGPT;
		try {
			messageGPT = new Message();
			messageGPT.setContent(result.getReply());
			messageGPT.setConversationId(dto.getConversationId());
			messageGPT.setDateCreate(new Date());
			messageGPT.setType(0);
			messageRepo.save(messageGPT);
		} catch (Exception e) {
			logger.info("Lỗi khi lưu tin nhắn gpt! ");
			response.put("code", HttpServletResponse.SC_BAD_REQUEST);
			response.put("data", null);
			response.put("message", "Lỗi khi lưu tin nhắn gpt! ");
			return ResponseEntity.ok(response);
		}

		// Khi hội thoại 6, hoặc 7 câu hoặc có > 120 words thì đặt title

		List<Message> longMessage = this.getMessageByConversationId(dto.getConversationId());
		Integer size = longMessage.size();
		String longMessageContent = "";
		String updateTitle = "";
		for(Message item : longMessage) {
			longMessageContent += item.getContent() + ";";
		}
		if ((size > 5) || longMessageContent.length() > 120) {
			updateTitle = conversationService.updateTitle(dto.getConversationId());
			logger.info(updateTitle);
		}
		
		if(updateTitle.equals("This conversation had updated title!"))
			updateTitle = "";

		response.put("code", HttpServletResponse.SC_OK);
		response.put("data", messageConverter.toDto(messageGPT));
		response.put("title_change", updateTitle);
		
		return ResponseEntity.ok(response);
	}

	@Override
	public PaginationDto getPageMessageByConversationId(String id, Integer page, Integer limit) {
		List<Message> messages = messageRepo.findByConversationId(id);
		List<Message> sortedList = messages.stream().sorted(Comparator.comparing(Message::getDateCreate))
				.collect(Collectors.toList());

		PaginationDto result = pagination.toPage(sortedList, page, limit);

		return result;
	}

	@Override
	public List<Message> getMessageByConversationId(String id) {
		return messageRepo.findByConversationId(id);
	}

}
