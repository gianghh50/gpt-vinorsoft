package com.vinorsoft.gpt.service.chat.repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vinorsoft.gpt.service.chat.entity.LoginHistory;

@Repository
public interface LoginHistoryRepo extends JpaRepository<LoginHistory, UUID> {

	@Query("select a from LoginHistory a where a.username = :username order by a.time desc")
	List<LoginHistory> getHistoryByUsername(@Param("username") String username);
	
	@Query("select a from LoginHistory a order by a.time desc")
	List<LoginHistory> getHistory();
	
	@Query("select a from LoginHistory a where (a.time BETWEEN :start AND :end) order by a.time")
	List<LoginHistory> getHistory(@Param("start") Date start, @Param("end") Date end);
	
}
