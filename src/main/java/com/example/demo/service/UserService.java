package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.controller.LoginController;

@Service
public class UserService {
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public boolean userIsPresent(String username) {
		Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE username = ?", Integer.class,
				username);

		if (count == null || count == 0) {
			logger.debug("User not found: " + username);
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

	public boolean addUser(String username, String pass) {
		boolean isPresent = userIsPresent(username);
		if (!isPresent) {
			String encodedPassword = passwordEncoder.encode(pass);

			String sql = "INSERT into users (username,password,enabled) values(?,?,?)";
			int added = jdbcTemplate.update(sql, username, encodedPassword, 1);

			String insertAuth = "INSERT Into authorities (username,authority) VALUES(?,?)";
			int inserted = jdbcTemplate.update(insertAuth, username, "ROLE_USER");

			if (added > 0 && inserted > 0) {
				logger.debug("New user inserted...");
				return true;
			}
		}
		return false; // User is already present
	}

}
