package com.vinorsoft.gpt.service.chat.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vinorsoft.gpt.service.chat.entity.Account;

@Repository
public interface AccountRepo extends JpaRepository<Account, Integer>{

    Optional<Account> findByUsername(String username);
    
    List<Account> findByEmail(String email);
    
    List<Account> findByResetPasswordToken(String resetPasswordToken);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
    
    @Query("select a from Account a where (a.dateCreate BETWEEN :start AND :end) order by a.dateCreate")
	List<Account> getAccounts(@Param("start") Date start, @Param("end") Date end);
}
