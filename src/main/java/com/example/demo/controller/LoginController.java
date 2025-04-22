package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.service.UserService;

@Controller
public class LoginController {

	@GetMapping("/login")
	public String login() {
		return "login"; // returns login.html
	}

	@GetMapping("/forgotPass")
	public String forgotPass() {
		return "resetPassword";
	}

	@GetMapping("/resetPass")
	public String showResetForm() {
		return "resetPassword";
	}

	@Autowired
	private UserService userService;

	@PostMapping("/resetPass")
	public String resetPassword(@RequestParam String username, @RequestParam String password,
			@RequestParam String cpassword, RedirectAttributes redirectAttributes) {

		System.out.println("resetPassword ===");

		if (username == null || username.trim().isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "Username cannot be empty.");
			return "redirect:/resetPass";
		}

		if (!password.equals(cpassword)) {
			redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
			return "redirect:/resetPass";
		}

		boolean success = userService.updatePassword(username, password);

		if (success) {
			redirectAttributes.addFlashAttribute("successMessage", "Password reset successfully!");
		} else {
			redirectAttributes.addFlashAttribute("errorMessage", "User not found or update failed.");
		}

		return "redirect:/resetPass";
	}
}
