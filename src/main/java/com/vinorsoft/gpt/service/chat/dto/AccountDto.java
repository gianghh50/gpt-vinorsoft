package com.vinorsoft.gpt.service.chat.dto;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {

    private Integer id;
    private String username;
    private Integer loginAttemps;
    private Integer isAccountNonLocked;
    private Timestamp loginLockUntil;
}
