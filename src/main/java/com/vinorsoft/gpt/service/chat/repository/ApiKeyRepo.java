package com.vinorsoft.gpt.service.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vinorsoft.gpt.service.chat.entity.ApiKey;

@Repository
public interface ApiKeyRepo extends JpaRepository<ApiKey, Integer> {

}
