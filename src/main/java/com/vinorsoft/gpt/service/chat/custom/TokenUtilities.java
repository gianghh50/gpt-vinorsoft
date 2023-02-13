package com.vinorsoft.gpt.service.chat.custom;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class TokenUtilities {

	public Map getClaimsProperty(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		String token = header.replace("Bearer", "");
		String[] split_string = token.split("\\.");
		String base64EncodedBody = split_string[1];
		org.apache.commons.codec.binary.Base64 base64Url = new org.apache.commons.codec.binary.Base64(true);
		String body = new String(base64Url.decode(base64EncodedBody));
		// Convert to HashMap
		Gson gson = new Gson();
		Map claims = gson.fromJson(body, Map.class);
		return claims;
	}
	
	public boolean IsAuthorizeUser(String username, HttpServletRequest request) {
		String token_username = this.getClaimsProperty(request).get("sub").toString();
		String token_role = this.getClaimsProperty(request).get("authorities").toString();
		String role = token_role.substring(1, token_role.length() - 1);

		if (role.equals("ROLE_admin") || token_username.equals(username))
			return true;
		return false;
	}
}
