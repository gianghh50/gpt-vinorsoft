package com.vinorsoft.gpt.service.chat.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vinorsoft.gpt.service.chat.entity.Account;
import com.vinorsoft.gpt.service.chat.repository.AccountRepo;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  AccountRepo accountRepo;
  
  @Autowired
	private BCryptPasswordEncoder encoder;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Account account = accountRepo.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        if(!account.getPassword().startsWith("$2a$")){
          account.setPassword(encoder.encode(account.getPassword()));
        } 
          return UserDetailsImpl.build(account);
  }

}