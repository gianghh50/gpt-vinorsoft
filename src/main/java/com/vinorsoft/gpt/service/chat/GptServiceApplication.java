package com.vinorsoft.gpt.service.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

@SpringBootApplication
//@OpenAPIDefinition( 
//    servers = {
//       @Server(url = "/auth/", description = "Auth Server URL")
//    }
//)
public class GptServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GptServiceApplication.class, args);
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
