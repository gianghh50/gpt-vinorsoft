package com.vinorsoft.gpt.service.chat.dto.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    @NotBlank
    private String token;

	@NotBlank
	private String password;
}
