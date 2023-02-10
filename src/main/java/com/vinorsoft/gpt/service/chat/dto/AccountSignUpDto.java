package com.vinorsoft.gpt.service.chat.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountSignUpDto {
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
}

