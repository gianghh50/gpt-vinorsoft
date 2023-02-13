package com.vinorsoft.gpt.service.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vinorsoft.gpt.service.chat.entity.AcceptedMail;

public interface AcceptedMailRepo extends JpaRepository<AcceptedMail, Integer> {

	List<AcceptedMail> findByEmail(String email);
}
