package com.vinorsoft.gpt.service.chat.dto.response;

import java.util.List;

public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private Integer id;
  private String username;
  private String email;
  private String avatar;
  private Integer isActivated;
  private String message;
  private String action;
  private List<String> roles;

  public JwtResponse(String accessToken, Integer id, String username, String email, String avatar, Integer isActivated, String message, String action, List<String> roles) {
    this.token = accessToken;
    this.id = id;
    this.username = username;
    this.email = email;
    this.roles = roles;
    this.avatar = avatar;
    this.isActivated = isActivated;
    this.message = message;
    this.action = action;
  }

  public String getAccessToken() {
    return token;
  }

  public void setAccessToken(String accessToken) {
    this.token = accessToken;
  }

  public String getTokenType() {
    return type;
  }

  public void setTokenType(String tokenType) {
    this.type = tokenType;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public List<String> getRoles() {
    return roles;
  }

public String getAvatar() {
	return avatar;
}

public void setAvatar(String avatar) {
	this.avatar = avatar;
}

public Integer getIsActivated() {
	return isActivated;
}

public void setIsActivated(Integer isActivated) {
	this.isActivated = isActivated;
}

public String getMessage() {
	return message;
}

public void setMessage(String message) {
	this.message = message;
}

public String getAction() {
	return action;
}

public void setAction(String action) {
	this.action = action;
}
}
