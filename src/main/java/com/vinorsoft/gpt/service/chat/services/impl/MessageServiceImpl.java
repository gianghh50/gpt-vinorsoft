package com.vinorsoft.gpt.service.chat.services.impl;

import java.time.Duration;
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

import com.mysql.cj.x.protobuf.MysqlxCursor.Open;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.vinorsoft.gpt.service.chat.converter.MessageConverter;
import com.vinorsoft.gpt.service.chat.custom.OpenAiApi;
import com.vinorsoft.gpt.service.chat.custom.Pagination;
import com.vinorsoft.gpt.service.chat.custom.ResponseFormat;
import com.vinorsoft.gpt.service.chat.dto.GPTResponseDto;
import com.vinorsoft.gpt.service.chat.dto.MessageDto;
import com.vinorsoft.gpt.service.chat.dto.OpenAiResponse;
import com.vinorsoft.gpt.service.chat.dto.PaginationDto;
import com.vinorsoft.gpt.service.chat.entity.Message;
import com.vinorsoft.gpt.service.chat.repository.ApiKeyRepo;
import com.vinorsoft.gpt.service.chat.repository.MessageRepo;
import com.vinorsoft.gpt.service.chat.services.interfaces.ApiKeyService;
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

	@Autowired
	ApiKeyService apiKeyService;
	
	@Autowired
	ResponseFormat responseFormat;
	
	@Autowired
	OpenAiApi openAiApi;

	@Override
	public ResponseEntity<Object> save(MessageDto dto) {

		Map<String, Object> response = new HashMap<>();

		// Lưu message người dùng gửi lên
		Message message;
		try {
			message = messageConverter.toEntity(dto);
			message.setDateCreate(new Date());
			messageRepo.save(message);
		} catch (Exception e) {
			logger.info("Error save message : " + e.toString());
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null, "Lỗi khi lưu tin nhắn user!");
		}

		OpenAiResponse answer = openAiApi.openAiDoThis(dto.getContent());

		if (answer.getSuccess() == 0) {
			messageRepo.delete(message);
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null, "Có lỗi xảy ra. Xin vui lòng thử lại");
		}
		Message messageGPT;
		try {
			messageGPT = new Message();
			messageGPT.setContent(answer.getText());
			messageGPT.setConversationId(dto.getConversationId());
			messageGPT.setDateCreate(new Date());
			messageGPT.setType(0);
			messageRepo.save(messageGPT);
		} catch (Exception e) {
			logger.info("Error save gpt answer: " + e.toString());
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null, "Có lỗi xảy ra. Xin vui lòng thử lại");
		}

		// Khi hội thoại 6, hoặc 7 câu hoặc có > 120 words thì đặt title

		List<Message> longMessage = this.getMessageByConversationId(dto.getConversationId());
		Integer size = longMessage.size();
		String longMessageContent = "";
		String updateTitle = "";
		for (Message item : longMessage) {
			longMessageContent += item.getContent() + ";";
		}
		if ((size > 5) || longMessageContent.length() > 120) {
			updateTitle = conversationService.updateTitle(dto.getConversationId());
			logger.info(updateTitle);
		}

		if (updateTitle.equals("This conversation had updated title!"))
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
