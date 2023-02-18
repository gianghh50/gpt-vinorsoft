package com.vinorsoft.gpt.service.chat.custom;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.vinorsoft.gpt.service.chat.dto.OpenAiResponse;
import com.vinorsoft.gpt.service.chat.services.interfaces.ApiKeyService;

@Component
public class OpenAiApi {
	
	private static final Logger logger = LoggerFactory.getLogger(OpenAiApi.class);
	
	@Autowired
	ApiKeyService apiKeyService;

	@SuppressWarnings("deprecation")
	public OpenAiResponse openAiDoThis(String question) {
		boolean success = false;
		String result = "Không thể kết nối. Vui lòng thử lại!";
		OpenAiService service;
		Integer count = 0;
		CompletionRequest completionRequest = CompletionRequest.builder().model("text-davinci-003").prompt(question)
				.maxTokens(1000).temperature(0.3).echo(false).build();

		do {
			String apiKey = apiKeyService.getRandom().getKey();
			service = new OpenAiService(apiKey, Duration.ofSeconds(120));
			try {
				result = service.createCompletion(completionRequest).getChoices().get(0).getText().trim();
				success = true;
			} catch (Exception e) {
				logger.error("Error request API: " + e.toString());
				success = false;
				count++;
				if (count == 3) {
					break;
				}
			}

		} while (!success);

		return new OpenAiResponse(success ? 1 : 0, result);
	}
}
