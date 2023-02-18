package com.vinorsoft.gpt.service.chat.services.impl;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.vinorsoft.gpt.service.chat.custom.Pagination;
import com.vinorsoft.gpt.service.chat.custom.ResponseFormat;
import com.vinorsoft.gpt.service.chat.dto.PaginationDto;
import com.vinorsoft.gpt.service.chat.entity.AcceptedMail;
import com.vinorsoft.gpt.service.chat.repository.AcceptedMailRepo;
import com.vinorsoft.gpt.service.chat.services.interfaces.AcceptedMailService;
@Service
public class AcceptedMailServiceImpl implements AcceptedMailService {

	private static final Logger logger = LoggerFactory.getLogger(AcceptedMailServiceImpl.class);
	@Autowired
	AcceptedMailRepo acceptedMailRepo;
	
	@Autowired
	ResponseFormat responseFormat;
	
	@Autowired
	Pagination pagination;
	
	@Override
	public ResponseEntity<Object> addMail(AcceptedMail mail) {
		try {
			mail.setId(null);
			acceptedMailRepo.save(mail);
			logger.info("Registed email: " + mail.getEmail());
			return responseFormat.response(HttpServletResponse.SC_OK, null, "Đăng ký email thành công!");
		}
		catch (Exception e) {
			logger.info("Registed email failed: " + mail.getEmail() + ". Error: " + e.toString());
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null, "Đăng ký email thất bại!");
		}
	}

	@Override
	public ResponseEntity<Object> deleteMail(Integer id) {
		try {
			acceptedMailRepo.deleteById(id);
			logger.info("Deleted email");
			return responseFormat.response(HttpServletResponse.SC_OK, null, "Xóa email thành công!");
		}
		catch (Exception e) {
			logger.info("Registed email failed: " + e.toString());
			return responseFormat.response(HttpServletResponse.SC_BAD_REQUEST, null, "Đăng ký email thất bại!");
		}
	}

	@Override
	public PaginationDto listMails(Integer page, Integer limit) {
		List<AcceptedMail> lisAcceptedMails = acceptedMailRepo.findAll();
		return pagination.toPage(lisAcceptedMails, page, limit);
	}

	
}
