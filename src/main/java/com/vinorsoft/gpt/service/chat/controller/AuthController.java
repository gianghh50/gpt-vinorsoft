package com.vinorsoft.gpt.service.chat.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.vinorsoft.gpt.service.chat.custom.ResponseFormat;
import com.vinorsoft.gpt.service.chat.custom.TokenUtilities;
import com.vinorsoft.gpt.service.chat.dto.AccountSignUpDto;
import com.vinorsoft.gpt.service.chat.dto.RefreshTokenResponse;
import com.vinorsoft.gpt.service.chat.dto.request.EmailRequest;
import com.vinorsoft.gpt.service.chat.dto.request.LoginRequest;
import com.vinorsoft.gpt.service.chat.dto.request.ResetPasswordRequest;
import com.vinorsoft.gpt.service.chat.dto.response.JwtResponse;
import com.vinorsoft.gpt.service.chat.repository.AccountRepo;
import com.vinorsoft.gpt.service.chat.repository.ConversationRepo;
import com.vinorsoft.gpt.service.chat.security.JwtConfig;
import com.vinorsoft.gpt.service.chat.security.jwt.JwtUtils;
import com.vinorsoft.gpt.service.chat.security.services.UserDetailsImpl;
import com.vinorsoft.gpt.service.chat.services.interfaces.AccountService;
import com.vinorsoft.gpt.service.chat.services.interfaces.ConversationService;
import com.vinorsoft.gpt.service.chat.services.interfaces.LoginHistoryService;
import com.vinorsoft.gpt.service.chat.services.interfaces.MessageService;

import io.jsonwebtoken.impl.DefaultClaims;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/authentication")
@Tag(name = "Authentication Controller")
@CrossOrigin(origins = "http://service4all.vinorsoft.com/")
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
	ResponseFormat responseFormat;
	
	@Autowired
	ConversationService conversationService;
	
	@Autowired
	LoginHistoryService loginHistoryService;
	
	@Autowired
	MessageService messageService;

	@Autowired
	JwtUtils jwtUtils;
	
	@Autowired
	TokenUtilities tokenUtilities;
	
	private final JwtConfig jwtConfig = new JwtConfig();

	@PostMapping("/login")
	public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {

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

		String message = "????ng nh???p th??nh c??ng!";
		String action = "";
		
		String device = request.getHeader("User-Agent");
		String clientIP = request.getRemoteAddr();
		

		if (userDetails.getIsActivated() == 0) {
			Map<String, Object> response = new HashMap<>();
			logger.info("T??i kho???n ch??a ???????c k??ch ho???t ho???c kh??ng t???n t???i!");
			response.put("code", HttpServletResponse.SC_FORBIDDEN);
			response.put("data", "unactive");
			response.put("message", "T??i kho???n ch??a ???????c k??ch ho???t!");
			
			loginHistoryService.save(userDetails.getUsername(), device, clientIP, "Account inactivated", new Date());
			
			return ResponseEntity.ok(response);
		}

		if (userDetails.getIsActivated() == 1
				&& (new Date(userDetails.getDateCreate().getTime() + 7 * 24 * 60 * 60 * 1000)).before(new Date())) {
			logger.info("B???n c???n c???p nh???t th??ng tin t??i kho???n ????? ti???p t???c s??? d???ng d???ch v???!");
			action = "update_infor";
			message = "B???n c???n c???p nh???t th??ng tin t??i kho???n ????? ti???p t???c s??? d???ng d???ch v???!";
			
			loginHistoryService.save(userDetails.getUsername(), device, clientIP, "Missing Information", new Date());
		}
		
		loginHistoryService.save(userDetails.getUsername(), device, clientIP, "Login Success", new Date());

		logger.info("Delete " + conversationService.deleteBlankConversation(userDetails.getUsername()) + " blank conversation!");
		
		return ResponseEntity.ok().headers(responseHeaders)
				.body(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(),
						userDetails.getAvatar(), userDetails.getIsActivated(), message, action, roles));
	}

	@PostMapping("/sign_up")
	public ResponseEntity<Object> signUp(@Valid @RequestBody AccountSignUpDto dto) {
		return accountService.signUp(dto);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GetMapping("/refreshtoken")
	public ResponseEntity<?> refreshtoken(HttpServletRequest request) throws Exception {
		Map claims = tokenUtilities.getClaimsProperty(request);
		String new_token = jwtUtils.doGenerateRefreshToken(claims, claims.get("sub").toString());
		return ResponseEntity.ok(new RefreshTokenResponse(new_token));
	}

	public Map<String, Object> getMapFromIoJsonwebtokenClaims(DefaultClaims claims) {
		Map<String, Object> expectedMap = new HashMap<String, Object>();
		for (Entry<String, Object> entry : claims.entrySet()) {
			expectedMap.put(entry.getKey(), entry.getValue());
		}
		return expectedMap;
	}
	@PostMapping("/logout")
	public ResponseEntity<Object> logOut() {
		Map<String, Object> response = new HashMap<>();
		logger.info("????ng xu???t th??nh c??ng!");
		response.put("code", HttpServletResponse.SC_OK);
		response.put("data", null);
		response.put("message", "????ng xu???t th??nh c??ng!");
		return ResponseEntity.ok(response);
	}

	@PostMapping("/validation_otp")
	public ResponseEntity<Object> validOTP(@RequestParam(name = "username", required = true) String username,
			@RequestParam(name = "limit", required = true) String OTP) {

		return accountService.validOTP(username, OTP);
	}

	private String base_url(String url) {
		int index = 0;
		int count = 0;
		while (count < 3) {
		    index = url.indexOf("/", index + 1);
		    count++;
		}
		return url.substring(0, index + 1);
	}
	
	@PostMapping("/forgot_password")
	public ResponseEntity<Object> forgotPassword(@RequestBody EmailRequest emailRequest, HttpServletRequest request)
			throws MailException, UnsupportedEncodingException, MessagingException {
		ResponseEntity<Object> response = accountService.forgotPassword(emailRequest.getEmail());
		String temp = response.getBody().toString();
		//String base_url = this.base_url(request.getRequestURL().toString());
		String base_url = "http://117.4.247.68:18201/resetpassword/";
		String result = "";
		try {
			Gson gson = new Gson();
			Map claims = gson.fromJson(temp, Map.class);
			result = claims.get("data").toString();
		}
		catch (Exception e) {
			result = temp;
		}
		if (!result.equals("error")) {
			String resetPasswordLink = base_url + temp;
			try {
				accountService.sendForgotPasswordEmail(emailRequest.getEmail(), resetPasswordLink);
			} catch (SendFailedException e) {
				logger.info("Error when forgot password: " + e.toString());
				return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null, "L???i khi g???i email!");
			}
			return responseFormat.response(HttpServletResponse.SC_OK, null, "Y??u c???u ?????i m???t kh???u th??nh c??ng!\nVui l??ng ki???m tra email ????? ti???p t???c!");
		}
		return response;
	}
	
	
	@PostMapping("/reset_password")
	public ResponseEntity<Object> resetPassword(@RequestBody ResetPasswordRequest request) {
		String password = request.getPassword();
		String token = request.getToken();
		if (password == null || password.equals("")) {
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null, "M???t kh???u kh??ng h???p l???!");
		}
		if (token == null || token.equals("")) {
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null, "Token kh??ng h???p l???!");
		}

		return accountService.resetPassword(token, password);
	}
}
