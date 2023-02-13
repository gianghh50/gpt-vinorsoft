package com.vinorsoft.gpt.service.chat.services.interfaces;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import org.springframework.http.ResponseEntity;

import com.vinorsoft.gpt.service.chat.dto.AccountInfoDto;
import com.vinorsoft.gpt.service.chat.dto.AccountSignUpDto;
import com.vinorsoft.gpt.service.chat.dto.AccountUpdateInforDto;
import com.vinorsoft.gpt.service.chat.dto.PaginationDto;
import com.vinorsoft.gpt.service.chat.entity.Account;


public interface AccountService {

    ResponseEntity<Object> forgotPassword(String email);

    void sendForgotPasswordEmail(String email, String resetPasswordLink) throws MessagingException, UnsupportedEncodingException;

    ResponseEntity<String> resetPassword(String token, String password);
	
    ResponseEntity<Object> findByUsername(String username);
	
	ResponseEntity<Object> signUp(AccountSignUpDto signUpDto);
	
	ResponseEntity<Object> validOTP(String username, String OTP);
	
	ResponseEntity<Object> updateInfo(AccountUpdateInforDto dto);
	
	PaginationDto getAccounts(Integer page, Integer limit);
	
	ResponseEntity<Object> updateStatus(String username, Integer status);
	
	ResponseEntity<Object> findAccount(String username);

}
