package com.vinorsoft.gpt.service.chat.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vinorsoft.gpt.service.chat.dto.AccountSignUpDto;
import com.vinorsoft.gpt.service.chat.dto.request.EmailRequest;
import com.vinorsoft.gpt.service.chat.dto.request.LoginRequest;
import com.vinorsoft.gpt.service.chat.dto.response.JwtResponse;
import com.vinorsoft.gpt.service.chat.repository.AccountRepo;
import com.vinorsoft.gpt.service.chat.security.jwt.JwtUtils;
import com.vinorsoft.gpt.service.chat.security.services.UserDetailsImpl;
import com.vinorsoft.gpt.service.chat.services.impl.AccountServiceImpl;
import com.vinorsoft.gpt.service.chat.services.interfaces.AccountService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/authentication")
@Tag(name = "Authentication Controller")
@CrossOrigin("http://localhost:3000")
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	AccountRepo accountRepo;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	AccountService accountService;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/login")
	public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		StringBuilder roleStr = new StringBuilder();

		int i = 0;
		for (String tmp : roles) {
			if (tmp.startsWith("ROLE_")) {
				roleStr.append(tmp.substring(5, tmp.length()));
				i++;
				if (i < roles.size())
					roleStr.append(", ");
			}
		}

		String str = roleStr.toString();

		HttpHeaders responseHeaders = new HttpHeaders();
		if (!str.equals(""))
			responseHeaders.set("Roles", str);
		responseHeaders.set("Authorization", "Bearer " + jwt);
		responseHeaders.set("Access-Control-Expose-Headers", "Authorization, Roles");

		String message = "Đăng nhập thành công!";
		String action = "";

		if (userDetails.getIsActivated() == 0) {
			Map<String, Object> response = new HashMap<>();
			logger.info("Tài khoản chưa được kích hoạt hoặc không tồn tại!");
			response.put("code", HttpServletResponse.SC_FORBIDDEN);
			response.put("data", "unactive");
			response.put("message", "Tài khoản chưa được kích hoạt!");
			return ResponseEntity.ok(response);
		}

		if (userDetails.getIsActivated() == 1
				&& (new Date(userDetails.getDateCreate().getTime() + 7 * 24 * 60 * 60 * 1000)).after(new Date())) {
			logger.info("Bạn cần cập nhật thông tin tài khoản để tiếp tục sử dụng dịch vụ!");
			action = "update_infor";
			message = "Bạn cần cập nhật thông tin tài khoản để tiếp tục sử dụng dịch vụ!";
		}

		return ResponseEntity.ok().headers(responseHeaders)
				.body(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(),
						userDetails.getAvatar(), userDetails.getIsActivated(), message, action, roles));
	}

	@PostMapping("/sign_up")
	public ResponseEntity<Object> signUp(@Valid @RequestBody AccountSignUpDto dto) {
		return accountService.signUp(dto);
	}

	@PostMapping("/logout")
	public ResponseEntity<Object> logOut() {
		Map<String, Object> response = new HashMap<>();
		logger.info("Đăng xuất thành công!");
		response.put("code", HttpServletResponse.SC_OK);
		response.put("data", null);
		response.put("message", "Đăng xuất thành công!");
		return ResponseEntity.ok(response);
	}

	@PostMapping("/validation_otp")
	public ResponseEntity<Object> validOTP(@RequestParam(name = "username", required = true) String username,
			@RequestParam(name = "limit", required = true) String OTP) {

		return accountService.validOTP(username, OTP);
	}

	@PostMapping("/forgot_password")
	public ResponseEntity<String> forgotPassword(@RequestBody EmailRequest emailRequest)
			throws MailException, UnsupportedEncodingException, MessagingException {
		ResponseEntity<String> response = accountService.forgotPassword(emailRequest.getEmail());
		String temp = response.getBody();
		if (temp != null && !temp.startsWith("Invalid")) {
			String resetPasswordLink = "http://localhost:10112/auth/reset-pass/" + temp;
			try {
				accountService.sendForgotPasswordEmail(emailRequest.getEmail(), resetPasswordLink);
			} catch (SendFailedException e) {
				return new ResponseEntity<>(e.getCause().toString(), HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>("email sent!", HttpStatus.OK);
		}
		return response;
	}
}