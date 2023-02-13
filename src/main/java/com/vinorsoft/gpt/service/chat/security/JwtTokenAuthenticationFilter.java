package com.vinorsoft.gpt.service.chat.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.google.gson.Gson;


public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {


	private final JwtConfig jwtConfig;

	public JwtTokenAuthenticationFilter(JwtConfig jwtConfig) {
		this.jwtConfig = jwtConfig;
	}


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		// 1. get the authentication header. Tokens are supposed to be passed in the
		// authentication header
		String header = request.getHeader(jwtConfig.getHeader());

		// 2. validate the header and check the prefix
		if (header == null || !header.startsWith(jwtConfig.getPrefix())) {
			chain.doFilter(request, response); // If not valid, go to the next filter.
			return;
		}

		// If there is no token provided and hence the user won't be authenticated.
		// It's Ok. Maybe the user accessing a public path or asking for a token.

		// All secured paths that needs a token are already defined and secured in
		// config class.
		// And If user tried to access without access token, then he won't be
		// authenticated and an exception will be thrown.

		// 3. Get the token
		String token = header.replace(jwtConfig.getPrefix(), "");
		if (isTokenExpired(token)) {
			try {
				logger.info("Token expired!");
				Map<String, Object> rs = new HashMap<>();
				rs.put("code", HttpServletResponse.SC_UNAUTHORIZED);
				rs.put("data", false);
				rs.put("message", "Token expired!");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				// Set the content type of the response
				response.setContentType("application/json");
				Gson gson = new Gson();
				PrintWriter writer = response.getWriter();
				writer.print(gson.toJson(rs));
				writer.flush();
				SecurityContextHolder.clearContext();
			} catch (Exception e) {
				SecurityContextHolder.clearContext();
			}
			return;
		} else
			try { // exceptions might be thrown in creating the claims if for example the token is
					// expired
					// 4. Validate the token
				Map claims = getClaims(token);

				String username = claims.get("sub").toString();
				if ((username != null)) {
					@SuppressWarnings("unchecked")
					// Get list roles
					List<String> authorities = new ArrayList<>();
					String roles = claims.get("authorities").toString();
					roles = roles.substring(1, roles.length() - 1);
					try {
						authorities.add(roles);
					} catch (Exception e) {
						// TODO: handle exception
						authorities.add("ROLE_user");
					}
					// 5. Create auth object
					// UsernamePasswordAuthenticationToken: A built-in object, used by spring to
					// represent the current authenticated / being authenticated user.
					// It needs a list of authorities, which has type of GrantedAuthority interface,
					// where SimpleGrantedAuthority is an implementation of that interface
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null,
							authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

					// 6. Authenticate the user
					// Now, user is authenticated
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			} catch (Exception e) {
				// In case of failure. Make sure it's clear; so guarantee user won't be
				// authenticated
				SecurityContextHolder.clearContext();
			}
		// go to the next filter in the filter chain
		chain.doFilter(request, response);
	}

	@SuppressWarnings("rawtypes")
	private Map getClaims(String token) {
		try {
			String[] split_string = token.split("\\.");
			String base64EncodedBody = split_string[1];
			org.apache.commons.codec.binary.Base64 base64Url = new org.apache.commons.codec.binary.Base64(true);
			String body = new String(base64Url.decode(base64EncodedBody));
			// Convert to HashMap
			Gson gson = new Gson();
			Map claims = gson.fromJson(body, Map.class);
			return claims;
		}
		catch (Exception e) {
			logger.info("Get Claims error: " + e.toString());
			return null;
		}
	}

	private boolean isTokenExpired(String token) {
		Map claims = getClaims(token);
		BigDecimal a = new BigDecimal(claims.get("exp").toString());
		long timeExpired  = a.longValueExact() * 1000;
		long timeNow = System.currentTimeMillis();
		if(timeExpired <= timeNow) return true;
		return false;
	}
}