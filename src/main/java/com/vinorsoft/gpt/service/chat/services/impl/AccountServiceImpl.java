package com.vinorsoft.gpt.service.chat.services.impl;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vinorsoft.gpt.service.chat.converter.AccountInforConverter;
import com.vinorsoft.gpt.service.chat.custom.Pagination;
import com.vinorsoft.gpt.service.chat.custom.ResponseFormat;
import com.vinorsoft.gpt.service.chat.dto.AccountConverter;
import com.vinorsoft.gpt.service.chat.dto.AccountInfoDto;
import com.vinorsoft.gpt.service.chat.dto.AccountSignUpDto;
import com.vinorsoft.gpt.service.chat.dto.AccountUpdateInforDto;
import com.vinorsoft.gpt.service.chat.dto.PaginationDto;
import com.vinorsoft.gpt.service.chat.dto.StatisticDto;
import com.vinorsoft.gpt.service.chat.entity.AcceptedMail;
import com.vinorsoft.gpt.service.chat.entity.Account;
import com.vinorsoft.gpt.service.chat.entity.LoginHistory;
import com.vinorsoft.gpt.service.chat.repository.AcceptedMailRepo;
import com.vinorsoft.gpt.service.chat.repository.AccountRepo;
import com.vinorsoft.gpt.service.chat.services.interfaces.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

	private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

	private static final long OTP_VALID_DURATION = 5 * 60 * 1000;

	public final Integer MAX_FAILED_ATTEMPTS = 5;

	private static final long RESET_PASSWORD_DURATION = 10 * 60 * 1000;

	@Autowired
	private AccountRepo accountRepo;

	@Autowired
	private AcceptedMailRepo acceptedMailRepo;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	AccountConverter accountConverter;

	@Autowired
	AccountInforConverter accountInforConverter;

	@Autowired
	Pagination pagination;

	@Autowired
	ResponseFormat responseFormat;

	@Override
	public ResponseEntity<Object> forgotPassword(String email) {
		List<Account> accounts = accountRepo.findByEmail(email);

		if (accounts.size() == 0) {
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, "error", "?????a ch??? email kh??ng ????ng!");
		}
		
		Account account = accounts.get(0);
		account.setResetPasswordToken(generateToken());
		account.setResetTokenCreate(new Date(System.currentTimeMillis()));
		account = accountRepo.save(account);

		return ResponseEntity.badRequest().body(account.getResetPasswordToken());

	}

	private String generateToken() {
		StringBuilder token = new StringBuilder();

		return token.append(UUID.randomUUID().toString()).append(UUID.randomUUID().toString()).toString();
	}

	@Override
	public void sendForgotPasswordEmail(String email, String resetPasswordLink)
			throws MessagingException, UnsupportedEncodingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("chatgpt@vinorsoft.com", "Chat GPT Vinorsoft Support");
		helper.setTo(email);

		String subject = "X??c nh???n y??u c???u ?????i m???t kh???u";

		String content = "<p>Xin ch??o,</p>" + "<p>Ch??ng t??i nh???n ???????c y??u c???u ?????i m???t kh???u c???a b???n.</p>"
				+ "<p>Vui l??ng nh???n v??o d?????ng link d?????i ????y ????? c???p nh???t m???t kh???u c???a b???n:</p>" + "<p><a href=\"" + resetPasswordLink
				+ "\">Y??u c???u ?????i m???t kh???u</a></p>" + "<br>" + "<p>Y??u c???u n??y c?? hi???u l???c trong 10 ph??t. B??? qua email n??y n???u b???n ???? nh??? m???t kh???u "
				+ "ho???c b???n kh??ng ph???i l?? ng?????i th???c hi???n y??u c???u n??y! Tr??n tr???ng.</p>";

		helper.setSubject(subject);

		helper.setText(content, true);

		mailSender.send(message);
	}

	@Override
	public ResponseEntity<Object> resetPassword(String token, String password) {
		List<Account> accounts = accountRepo.findByResetPasswordToken(token);

		if (accounts.size() == 0) {
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, "error", "M?? kh??ng h???p l???!");
		}

		Account account = accounts.get(0);

		if (new Date(account.getResetTokenCreate().getTime() + RESET_PASSWORD_DURATION).before(new Date())) {
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, "error", "???? qu?? h???n y??u c???u! Vui l??ng th???c hi???n l???i!");

		}

		account.setPassword(password);
		account.setResetPasswordToken(null);
		account.setResetTokenCreate(null);

		try {
			accountRepo.save(account);
		} catch (Exception e) {
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, "error", "L???i khi l??u m???t kh???u m???i. Vui long th??? l???i!");
		}

		return responseFormat.response(HttpServletResponse.SC_OK, null, "Thay ?????i m???t kh???u th??nh c??ng!");
	}

	@Override
	public ResponseEntity<Object> findByUsername(String username) {
		Map<String, Object> response = new HashMap<>();
		try {
			Account account = accountRepo.findByUsername(username)
					.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
			return ResponseEntity.ok(account);
		} catch (Exception e) {
			logger.info("User Not Found with username: " + username);
			response.put("code", HttpServletResponse.SC_BAD_REQUEST);
			response.put("data", null);
			response.put("message", "User Not Found with username: " + username);
			return ResponseEntity.ok(response);
		}
	}
	
	private boolean isVinorsoftMail(String mail) {
		if(mail.endsWith("@vinorsoft.com"))
			return true;
		return false;
	}

	@Override
	public ResponseEntity<Object> signUp(AccountSignUpDto signUpDto) {

		Map<String, Object> response = new HashMap<>();
		ResponseEntity<Object> accountRes = findByUsername(signUpDto.getUsername());
		try {
			Account account = (Account) accountRes.getBody();

			if (account.getIsActivated() == 0) {
				accountRepo.delete(account);
			} else {
				logger.info("Username " + signUpDto.getUsername() + " ???? t???n t???i!");
				return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, "duplicate_username",
						"Username " + signUpDto.getUsername() + " ???? t???n t???i!");
			}
		} catch (Exception e) {
		}

		// Ki???m tra email, ch???p nh???n mail c?? trong danh s??ch v?? mail vinorsoft
		try {
			List<Account> accounts = accountRepo.findByEmail(signUpDto.getEmail());
			if (accounts.size() != 0) {

				if (accounts.get(0).getIsActivated() == 0) {
					accountRepo.delete(accounts.get(0));
				} else {
					logger.info("?????a ch??? email " + signUpDto.getEmail() + " ???? ???????c s??? d???ng!");
					return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, "duplicate_mail",
							"?????a ch??? email: " + signUpDto.getEmail() + " ???? ???????c s??? d???ng!");
				}
			}
			List<AcceptedMail> mails = acceptedMailRepo.findByEmail(signUpDto.getEmail());
			if (mails.size() == 0 && !isVinorsoftMail(signUpDto.getEmail())) {
				logger.info("?????a ch??? email " + signUpDto.getEmail() + " kh??ng ???????c ????ng k??! \nVui l??ng li??n h??? b??? ph???n CSKH c???a Vinorsoft ????? ???????c h??? tr???!");
				return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, "unregisted_mail",
						"?????a ch??? email " + signUpDto.getEmail() + " kh??ng ???????c ????ng k??! \nVui l??ng li??n h??? b??? ph???n CSKH c???a Vinorsoft ????? ???????c h??? tr???!");
			}

		} catch (Exception e) {
		}

		Account account = new Account();
		account.setUsername(signUpDto.getUsername());
		account.setPassword(signUpDto.getPassword());
		account.setEmail(signUpDto.getEmail());
		account.setPhoneNumber(signUpDto.getPhoneNumber());
		account.setIsActivated(0);
		account.setRole("user");
		account.setDateCreate(new Date());
		// Create OTP
		account.setOTP(RandomStringUtils.random(6, false, true));

		try {
			accountRepo.save(account);
		} catch (Exception ex) {
			logger.info(ex.toString());
			logger.info("L???i khi t???o t??i kho???n !");
			response.put("code", HttpServletResponse.SC_BAD_REQUEST);
			response.put("data", null);
			response.put("message", "L???i khi t???o t??i kho???n !");
			return ResponseEntity.ok(response);
		}

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);

			helper.setFrom("vinorsoft@gmail.com", "Vinorsoft Support");
			helper.setTo(account.getEmail());

			String subject = "X??c th???c t??i kho???n chat GPT by Vinorsoft";

			String content = "<p>Xin ch??o,</p>" + "<p>C???m ??n b???n ???? ????ng k?? t??i kho???n chat GPT by Vinorsoft.</p>"
					+ "<p>????y l?? m?? OTP c???a t??i kho???n " + account.getUsername() + ":</p>" + "<p>" + account.getOTP()
					+ "</p>" + "<br>" + "<p>M?? OTP c?? th???i gian hi???u l???c 05 ph??t k??? t??? l??c t???o t??i kho???n. "
					+ "Ch??c b???n c?? nh???ng tr???i nghi???m t???t nh???t v???i chat GPT by Vinorsoft !</p>";

			helper.setSubject(subject);

			helper.setText(content, true);

			mailSender.send(message);
		} catch (Exception e) {
			logger.info("Kh??ng th??? g???i mail. Vui l??ng th??? l???i! Error: " + e.toString());
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null,
					"Kh??ng th??? g???i mail. Vui l??ng th??? l???i!");
		}

		logger.info("T???o t??i kho???n th??nh c??ng!");
		response.put("code", HttpServletResponse.SC_OK);
		response.put("data", null);
		response.put("message", "T???o t??i kho???n th??nh c??ng!");
		return responseFormat.response(HttpServletResponse.SC_OK, null, "T???o t??i kho???n th??nh c??ng!");
	}

	@Override
	public ResponseEntity<Object> validOTP(String username, String OTP) {

		Map<String, Object> response = new HashMap<>();

		ResponseEntity<Object> accountRes = findByUsername(username);
		Date now = new Date();
		try {
			Account account = (Account) accountRes.getBody();

			// T??i kho???n ???? k??ch ho???t
			if (account.getIsActivated() == 1) {
				logger.info("T??i kho???n ???? ???????c k??ch ho???t!");
				response.put("code", HttpServletResponse.SC_FORBIDDEN);
				response.put("data", null);
				response.put("message", "T??i kho???n ???? ???????c k??ch ho???t!");
				return ResponseEntity.ok(response);
			}
			// OTP qu?? h???n -> x??a t??i kho???n
			if (now.after(new Date(account.getDateCreate().getTime() + OTP_VALID_DURATION))) {
				accountRepo.delete(account);
				logger.info("OTP ???? qu?? h???n!");
				response.put("code", HttpServletResponse.SC_NOT_ACCEPTABLE);
				response.put("data", null);
				response.put("message", "OTP ???? qu?? h???n! Vui long ????ng k?? l???i!");
				return ResponseEntity.ok(response);
			}
			// Sai OTP
			if (!OTP.equals(account.getOTP())) {
				logger.info("M???t kh???u OTP kh??ng ????ng!");
				response.put("code", HttpServletResponse.SC_NOT_ACCEPTABLE);
				response.put("data", null);
				response.put("message", "M???t kh???u OTP kh??ng ????ng!");
				return ResponseEntity.ok(response);
			}

			account.setIsActivated(1);
			accountRepo.save(account);
			logger.info("X??c th???c OTP th??nh c??ng!");
			response.put("code", HttpServletResponse.SC_OK);
			response.put("data", null);
			response.put("message", "X??c th???c OTP th??nh c??ng!");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return accountRes;
		}
	}

	@Override
	public ResponseEntity<Object> updateInfo(AccountUpdateInforDto dto) {
		ResponseEntity<Object> accountRes = findByUsername(dto.getUsername());
		Account account;
		try {
			account = (Account) accountRes.getBody();
		} catch (Exception e) {
			return accountRes;
		}

		try {
			if (dto.hasBlankField()) {
				logger.info("Th??ng tin ch??a ?????y ?????!");
				return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null, "Th??ng tin ch??a ?????y ?????!");
			}
		} catch (IllegalAccessException e) {
			logger.info("Account infor update error: " + e.toString());
		}
		account = accountInforConverter.toSaveEntity(account, dto);
		account.setIsActivated(2);
		account.setDateModify(new Date());
		try {
			accountRepo.save(account);
		} catch (Exception e) {
			logger.info("Account infor update error: " + e.toString());
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null,
					"Account infor update error: " + e.toString());
		}
		logger.info("C???p nh???t th??ng tin th??nh c??ng!");
		return responseFormat.response(HttpServletResponse.SC_OK, accountInforConverter.toDto(account),
				"C???p nh???t th??ng tin th??nh c??ng!");
	}

	@Override
	public PaginationDto getAccounts(Integer page, Integer limit) {
		List<AccountInfoDto> result = new ArrayList<>();
		List<Account> accounts = accountRepo.findAll();
		for (Account item : accounts) {
			result.add(accountInforConverter.toDto(item));
		}
		return pagination.toPage(result, page, limit);
	}

	@Override
	public ResponseEntity<Object> updateStatus(String username, Integer status) {

		ResponseEntity<Object> accountRes = findByUsername(username);
		Account account;
		try {
			account = (Account) accountRes.getBody();
		} catch (Exception e) {
			return accountRes;
		}

		account.setIsActivated(status);
		account.setDateModify(new Date());

		try {
			accountRepo.save(account);
		} catch (Exception e) {
			logger.info("Account status update error: " + e.toString());
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null, "L???i khi c???p nh???t tr???ng th??i!");
		}
		logger.info("C???p nh???t tr???ng th??i th??nh c??ng!");
		return responseFormat.response(HttpServletResponse.SC_OK, null, "C???p nh???t tr???ng th??i th??nh c??ng!");
	}
	
	@Override
	public ResponseEntity<Object> updateRole(String username, String role) {

		ResponseEntity<Object> accountRes = findByUsername(username);
		Account account;
		try {
			account = (Account) accountRes.getBody();
		} catch (Exception e) {
			return accountRes;
		}

		account.setRole(role);
		account.setDateModify(new Date());

		try {
			accountRepo.save(account);
		} catch (Exception e) {
			logger.info("Account role update error: " + e.toString());
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null, "L???i khi c???p nh???t vai tr?? ng?????i d??ng!");
		}
		logger.info("Account role updated!");
		return responseFormat.response(HttpServletResponse.SC_OK, null, "C???p nh???t vai tr?? th??nh c??ng!");
	}

	@Override
	public ResponseEntity<Object> findAccount(String username) {
		ResponseEntity<Object> accountRes = findByUsername(username);
		Account account;
		try {
			account = (Account) accountRes.getBody();
		} catch (Exception e) {
			return accountRes;
		}

		return ResponseEntity.ok(accountInforConverter.toDto(account));
	}

	@Override
	public ResponseEntity<Object> updatePassword(String username, String password) {
		ResponseEntity<Object> accountRes = findByUsername(username);
		Account account;
		try {
			account = (Account) accountRes.getBody();
		} catch (Exception e) {
			return accountRes;
		}
		account.setPassword(password);
		account.setDateModify(new Date());
		try {
			accountRepo.save(account);
		} catch (Exception e) {
			logger.info("Account change password error: " + e.toString());
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null,
					"Account change password error: " + e.toString());
		}
		logger.info("T??i kho???n " + username + " thay ?????i m???t kh???u th??nh c??ng!");
		return responseFormat.response(HttpServletResponse.SC_OK, accountInforConverter.toDto(account),
				"Thay ?????i m???t kh???u th??nh c??ng!");
		
	}

	@Override
	public ResponseEntity<Object> quickSignUp(AccountSignUpDto signUpDto) {
		Map<String, Object> response = new HashMap<>();
		ResponseEntity<Object> accountRes = findByUsername(signUpDto.getUsername());
		try {
			Account account = (Account) accountRes.getBody();

			if (account.getIsActivated() == 0) {
				accountRepo.delete(account);
			} else {
				logger.info("Username " + signUpDto.getUsername() + " ???? t???n t???i!");
				return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, "duplicate_username",
						"Username " + signUpDto.getUsername() + " ???? t???n t???i!");
			}
		} catch (Exception e) {
		}

		// Ki???m tra email, ch???p nh???n mail c?? trong danh s??ch v?? mail vinorsoft
		try {
			List<Account> accounts = accountRepo.findByEmail(signUpDto.getEmail());
			if (accounts.size() != 0) {

				if (accounts.get(0).getIsActivated() == 0) {
					accountRepo.delete(accounts.get(0));
				} else {
					logger.info("?????a ch??? email " + signUpDto.getEmail() + " ???? ???????c s??? d???ng!");
					return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, "duplicate_mail",
							"?????a ch??? email: " + signUpDto.getEmail() + " ???? ???????c s??? d???ng!");
				}
			}
		} catch (Exception e) {
		}

		Account account = new Account();
		account.setUsername(signUpDto.getUsername());
		account.setPassword(signUpDto.getPassword());
		account.setEmail(signUpDto.getEmail());
		account.setPhoneNumber(signUpDto.getPhoneNumber());
		account.setIsActivated(2);
		account.setRole("user");
		account.setDateCreate(new Date());

		try {
			accountRepo.save(account);
		} catch (Exception ex) {
			logger.info(ex.toString());
			logger.info("L???i khi t???o t??i kho???n !");
			response.put("code", HttpServletResponse.SC_BAD_REQUEST);
			response.put("data", null);
			response.put("message", "L???i khi t???o t??i kho???n !");
			return ResponseEntity.ok(response);
		}
		logger.info("T???o t??i kho???n th??nh c??ng!");
		response.put("code", HttpServletResponse.SC_OK);
		response.put("data", null);
		response.put("message", "T???o t??i kho???n th??nh c??ng!");
		return responseFormat.response(HttpServletResponse.SC_OK, null, "T???o t??i kho???n th??nh c??ng!");
	}

	@Override
	@SuppressWarnings("deprecation")
	public List<StatisticDto> AccountStatistic(Integer months) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -months);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date start_date = calendar.getTime();
		List<StatisticDto> result = new ArrayList<>();
		for(Integer i = months - 1; i >= 0; i--) {
			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, - i);
			result.add(new StatisticDto(calendar.getTime(), 0));
		}
		List<Account> accounts = accountRepo.getAccounts(start_date, new Date());
		for(Account account:accounts) {
			for(StatisticDto item: result) {
				if(item.getDate().getMonth() == account.getDateCreate().getMonth() && item.getDate().getYear() == account.getDateCreate().getYear()) {
					item.setCount(item.getCount() + 1);
					break;
				}
			}
		}
		
		
		return result;
	}
}
