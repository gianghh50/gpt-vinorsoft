package com.vinorsoft.gpt.service.chat.dto.request;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequest {
    @NotBlank
    private String email;
}