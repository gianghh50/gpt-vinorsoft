package com.vinorsoft.gpt.service.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vinorsoft.gpt.service.chat.entity.AcceptedMail;

@Repository
public interface AcceptedMailRepo extends JpaRepository<AcceptedMail, Integer> {

	List<AcceptedMail> findByEmail(String email);
	
	void deleteById(Integer id);
}
