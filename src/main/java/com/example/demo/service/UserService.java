package com.example.demo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.bussinessObjects.User;
import com.example.demo.config.UserRowMapper;
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
			logger.debug("user is not present");
			return false; // User not present
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean addUser(String username, String pass, String email, String role) {
		boolean isPresent = userIsPresent(username);
		if (!isPresent) {
			String encodedPassword = passwordEncoder.encode(pass);

			String sql = "INSERT into users (username,password,enabled,email) values(?,?,?,?)";
			int added = jdbcTemplate.update(sql, username, encodedPassword, 1, email);

			String insertAuth = "INSERT Into authorities (username,authority) VALUES(?,?)";
			int inserted = jdbcTemplate.update(insertAuth, username, role);

			if (added > 0 && inserted > 0) {
				logger.debug("New user inserted...");
				return true;
			}
		}
		return false; // User is already present
	}

	public List<String> getAllUsernames() {
		String sql = "SELECT username FROM users";
		return jdbcTemplate.queryForList(sql, String.class); // return the single row
	}

	public List<User> getAllUserData() {
		String sql = "select * from users";
		List<User> userList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
		return userList;
	}

	public boolean deleteUser(String username) {
		boolean authorityTableExists = false;

		try {
			jdbcTemplate.queryForObject("SELECT 1 FROM authorities LIMIT 1", Integer.class);
			authorityTableExists = true;
		} catch (Exception ex) {
			logger.warn("Table 'authority' does not exist. Skipping authority deletion.");
		}

		if (authorityTableExists) {
			String deleteAuthorityQuery = "DELETE FROM authorities WHERE username = ?";
			int deletedAuth = jdbcTemplate.update(deleteAuthorityQuery, username);

			if (deletedAuth > 0) {
				logger.debug("Authority for user {} deleted successfully.", username);
			} else {
				logger.debug("No authority found for user {}. Proceeding to delete user.", username);
			}
		}

		String deletUserQuery = "delete from users where username=?";
		int deleted = jdbcTemplate.update(deletUserQuery, username);
		if (deleted > 0) {
			logger.debug("user is deleted successfully...");
			return true;
		}
		logger.debug("failed user deletion");
		return false;
	}

	public boolean editUser(User user) {
		logger.debug("inside editUser()");
		String encodedPassword = passwordEncoder.encode(user.getPassword());

		String sql = "UPDATE users SET password = ?, enabled = ? WHERE username = ?";
		int updated = jdbcTemplate.update(sql, encodedPassword, user.isEnabled(), user.getUsername());

		String updateAuthority = "update authorities set authority=? where username=?";
		int updatedAuth = jdbcTemplate.update(updateAuthority, user.getRole(), user.getUsername());

		if (updated > 0 && updatedAuth > 0) {
			logger.debug("User updated successfully");
			return true;
		}

		logger.debug("User updation failed");
		return false;
	}

	public User getUserByUsername(String username) {
		String sql = "SELECT u.username, u.password, u.enabled, a.authority, u.email FROM users u JOIN authorities a ON u.username = a.username where u.username=?";
		User user = jdbcTemplate.queryForObject(sql, new UserRowMapper(), username);
		logger.info("Fetched user: " + user.getUsername() + ", " + user.getPassword() + ", " + user.isEnabled() + ", "
				+ user.getRole());
		return user;
	}

}
