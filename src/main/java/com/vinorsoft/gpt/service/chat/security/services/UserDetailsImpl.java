package com.vinorsoft.gpt.service.chat.security.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vinorsoft.gpt.service.chat.entity.Account;

public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 1L;

	private Integer id;

	private String username;

	private String email;

	private Integer isActivated;
	
	private String avatar;
	
	private Date dateCreate;

	@JsonIgnore
	private String password;

	private Collection<? extends GrantedAuthority> authorities;

	public UserDetailsImpl(Integer id, String username, String email, String password, Integer isActivated, String avatar, Date dateCreate, 
			Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.authorities = authorities;
		this.isActivated = isActivated;
		this.avatar = avatar;
		this.dateCreate = dateCreate;
	}

	public static UserDetailsImpl build(Account account) {
		ArrayList<String> roles = new ArrayList<>();
		if (account.getRole() != null) {
			String[] x1 = account.getRole().split(",");
			for (int i = 0; i < x1.length; i++) {
				roles.add(x1[i].trim());
			}
		}
		List<GrantedAuthority> authorities = roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.collect(Collectors.toList());

		return new UserDetailsImpl(account.getId(), account.getUsername(), account.getEmail(), account.getPassword(),
				account.getIsActivated(), account.getAvatar(), account.getDateCreate(), authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public Integer getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}

	@Override
	public int hashCode() {
		return this.id;
	}

	public Integer getIsActivated() {
		return isActivated;
	}

	public String getAvatar() {
		return avatar;
	}

	public Date getDateCreate() {
		return dateCreate;
	}

}
