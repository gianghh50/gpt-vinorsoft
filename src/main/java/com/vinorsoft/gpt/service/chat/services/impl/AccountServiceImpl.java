package com.vinorsoft.gpt.service.chat.services.impl;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import com.vinorsoft.gpt.service.chat.entity.AcceptedMail;
import com.vinorsoft.gpt.service.chat.entity.Account;
import com.vinorsoft.gpt.service.chat.repository.AcceptedMailRepo;
import com.vinorsoft.gpt.service.chat.repository.AccountRepo;
import com.vinorsoft.gpt.service.chat.services.interfaces.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

	private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

	private static final long OTP_VALID_DURATION = 5 * 60 * 1000;

	public final Integer MAX_FAILED_ATTEMPTS = 5;

	private static final long LOCK_TIME_DURATION = 10 * 60 * 1000;

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
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null, "Địa chỉ email không đúng!");
		}
		
		Account account = accounts.get(0);
		account.setResetPasswordToken(generateToken());
		account.setResetTokenCreate(new Date());
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

		helper.setFrom("vinorsoft@gmail.com", "Vinorsoft Support");
		helper.setTo(email);

		String subject = "Here's the link to reset your password";

		String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
				+ "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + resetPasswordLink
				+ "\">Change my password</a></p>" + "<br>" + "<p>Ignore this email if you do remember your password, "
				+ "or you have not made the request.</p>";

		helper.setSubject(subject);

		helper.setText(content, true);

		mailSender.send(message);
	}

	@Override
	public ResponseEntity<String> resetPassword(String token, String password) {
//		Optional<Account> accountOptional = Optional.ofNullable(accountRepo.findByResetPassToken(token));
//
//		if (!accountOptional.isPresent()) {
//			return new ResponseEntity<>("Invalid email id.", HttpStatus.UNAUTHORIZED);
//		}
//
//		LocalDateTime tokenCreationDate = accountOptional.get().getTokenCreationDate();
//
//		if (isTokenExpired(tokenCreationDate)) {
//			return new ResponseEntity<>("Token expired.", HttpStatus.NOT_FOUND);
//
//		}
//
//		Account account = accountOptional.get();
//
//		account.setPassword(password);
//		account.setResetPassToken(null);
//		account.setTokenCreationDate(null);
//
//		accountRepo.save(account);

		return new ResponseEntity<>("Your password successfully updated.", HttpStatus.OK);
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
				logger.info("Username " + signUpDto.getUsername() + " đã tồn tại!");
				return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, "duplicate_username",
						"Username " + signUpDto.getUsername() + " đã tồn tại!");
			}
		} catch (Exception e) {
		}

		// Kiểm tra email, chấp nhận mail có trong danh sách và mail vinorsoft
		try {
			List<Account> accounts = accountRepo.findByEmail(signUpDto.getEmail());
			if (accounts.size() != 0) {

				if (accounts.get(0).getIsActivated() == 0) {
					accountRepo.delete(accounts.get(0));
				} else {
					logger.info("Địa chỉ email " + signUpDto.getEmail() + " đã được sử dụng!");
					return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, "duplicate_mail",
							"Địa chỉ email: " + signUpDto.getEmail() + " đã được sử dụng!");
				}
			}
			List<AcceptedMail> mails = acceptedMailRepo.findByEmail(signUpDto.getEmail());
			if (mails.size() == 0 && !isVinorsoftMail(signUpDto.getEmail())) {
				logger.info("Địa chỉ email " + signUpDto.getEmail() + " không được đăng ký! \nVui lòng liên hệ bộ phận CSKH của Vinorsoft để được hỗ trợ!");
				return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, "unregisted_mail",
						"Địa chỉ email " + signUpDto.getEmail() + " không được đăng ký! \nVui lòng liên hệ bộ phận CSKH của Vinorsoft để được hỗ trợ!");
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
			logger.info("Lỗi khi tạo tài khoản !");
			response.put("code", HttpServletResponse.SC_BAD_REQUEST);
			response.put("data", null);
			response.put("message", "Lỗi khi tạo tài khoản !");
			return ResponseEntity.ok(response);
		}

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);

			helper.setFrom("vinorsoft@gmail.com", "Vinorsoft Support");
			helper.setTo(account.getEmail());

			String subject = "Xác thực tài khoản chat GPT by Vinorsoft";

			String content = "<p>Xin chào,</p>" + "<p>Cảm ơn bạn đã đăng ký tài khoản chat GPT by Vinorsoft.</p>"
					+ "<p>Đây là mã OTP của tài khoản " + account.getUsername() + ":</p>" + "<p>" + account.getOTP()
					+ "</p>" + "<br>" + "<p>Mã OTP có thời gian hiệu lực 05 phút kể từ lúc tạo tài khoản. "
					+ "Chúc bạn có những trải nghiệm tốt nhất với chat GPT by Vinorsoft !</p>";

			helper.setSubject(subject);

			helper.setText(content, true);

			mailSender.send(message);
		} catch (Exception e) {
			logger.info("Không thể gửi mail. Vui lòng thử lại! Error: " + e.toString());
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null,
					"Không thể gửi mail. Vui lòng thử lại!");
		}

		logger.info("Tạo tài khoản thành công!");
		response.put("code", HttpServletResponse.SC_OK);
		response.put("data", null);
		response.put("message", "Tạo tài khoản thành công!");
		return responseFormat.response(HttpServletResponse.SC_OK, null, "Tạo tài khoản thành công!");
	}

	@Override
	public ResponseEntity<Object> validOTP(String username, String OTP) {

		Map<String, Object> response = new HashMap<>();

		ResponseEntity<Object> accountRes = findByUsername(username);
		Date now = new Date();
		try {
			Account account = (Account) accountRes.getBody();

			// Tài khoản đã kích hoạt
			if (account.getIsActivated() == 1) {
				logger.info("Tài khoản đã được kích hoạt!");
				response.put("code", HttpServletResponse.SC_FORBIDDEN);
				response.put("data", null);
				response.put("message", "Tài khoản đã được kích hoạt!");
				return ResponseEntity.ok(response);
			}
			// OTP quá hạn -> xóa tài khoản
			if (now.after(new Date(account.getDateCreate().getTime() + OTP_VALID_DURATION))) {
				accountRepo.delete(account);
				logger.info("OTP đã quá hạn!");
				response.put("code", HttpServletResponse.SC_NOT_ACCEPTABLE);
				response.put("data", null);
				response.put("message", "OTP đã quá hạn! Vui long đăng ký lại!");
				return ResponseEntity.ok(response);
			}
			// Sai OTP
			if (!OTP.equals(account.getOTP())) {
				logger.info("Mật khẩu OTP không đúng!");
				response.put("code", HttpServletResponse.SC_NOT_ACCEPTABLE);
				response.put("data", null);
				response.put("message", "Mật khẩu OTP không đúng!");
				return ResponseEntity.ok(response);
			}

			account.setIsActivated(1);
			accountRepo.save(account);
			logger.info("Xác thực OTP thành công!");
			response.put("code", HttpServletResponse.SC_OK);
			response.put("data", null);
			response.put("message", "Xác thực OTP thành công!");
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
				logger.info("Thông tin chưa đầy đủ!");
				return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null, "Thông tin chưa đầy đủ!");
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
		logger.info("Cập nhật thông tin thành công!");
		return responseFormat.response(HttpServletResponse.SC_OK, accountInforConverter.toDto(account),
				"Cập nhật thông tin thành công!");
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
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null, "Lỗi khi cập nhật trạng thái!");
		}
		logger.info("Cập nhật trạng thái thành công!");
		return responseFormat.response(HttpServletResponse.SC_OK, null, "Cập nhật trạng thái thành công!");
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

}
