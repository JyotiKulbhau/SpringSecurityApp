package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public boolean userIsPresent(String username) {
		Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE username = ?", Integer.class,
				username);

		if (count == null || count == 0) {
			System.out.println("User not found: " + username);
			return false;
		}
		return true;
	}

	public boolean updatePassword(String username, String rawPassword) {
		try {
			boolean isPresent = userIsPresent(username);
			if (isPresent) {
				String encodedPassword = passwordEncoder.encode(rawPassword);
				int rows = jdbcTemplate.update("UPDATE users SET password = ? WHERE username = ?", encodedPassword,
						username);
				return rows > 0;
			}
			return false; // User not present
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
