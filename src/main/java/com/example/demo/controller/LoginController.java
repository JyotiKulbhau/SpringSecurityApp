package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.bussinessObjects.User;
import com.example.demo.service.EmailService;
import com.example.demo.service.OtpService;
import com.example.demo.service.UserService;

@Controller
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private OtpService otpService;

	@GetMapping("/login")
	public String login() {
		logger.debug("redirecting to login");
		return "login"; // returns login.html
	}

	@GetMapping("/forgotPass")
	public String forgotPass() {
		return "resetPassword";
	}

	@GetMapping("/resetPass")
	public String showResetForm() {
		logger.debug("redirecting to resetPassword");
		return "resetPassword";
	}

	@PostMapping("/resetPass")
	public String resetPassword(@RequestParam String username, @RequestParam String password,
			@RequestParam String cpassword, RedirectAttributes redirectAttributes) {

		if (username == null || username.trim().isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "Username cannot be empty.");
			return "redirect:/resetPass";
		}

		if (!password.equals(cpassword)) {
			redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
			return "redirect:/resetPass";
		}

		User user = userService.getUserByUsername(username);
		if (user == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
			return "redirect:/resetPass";
		}

		boolean success = userService.updatePassword(username, password);

		if (success) {
			String subject = "Password Reset Confirmation";
			String message = "Hello " + username + ",\nYour password has been reset successfully.\n- Support Team";

			String to = user.getEmail();
			logger.debug("Sending email to " + user.getEmail());
			// Send confirmation email
			emailService.sendPasswordResetEmail(to, subject, message);

			redirectAttributes.addFlashAttribute("successMessage",
					"Password reset successfully! Please check your email.");
		} else {
			redirectAttributes.addFlashAttribute("errorMessage", "Password update failed or user is not present.");
		}

		return "redirect:/resetPass";
	}

}
