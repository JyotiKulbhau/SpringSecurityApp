package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.service.OtpService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/otp")
public class OtpController {

	@Autowired
	private OtpService otpService;
	/*
	 * @Value("${twilio.phone.number}") private String twilioNumber;
	 * 
	 * 
	 * @PostMapping("/send") public ResponseEntity<String> sendOtp(@RequestParam
	 * String phone) { String otp = otpService.generateAndSaveOtp(phone);
	 * 
	 * String formattedPhone = phone.replaceAll("\\s+", "").trim(); if
	 * (!formattedPhone.startsWith("+")) { formattedPhone = "+91" + formattedPhone;
	 * // add country code if missing }
	 * 
	 * Message.creator( new PhoneNumber(formattedPhone), // To new
	 * PhoneNumber(twilioNumber), // From (Twilio) "Your OTP is: " + otp ).create();
	 * 
	 * return ResponseEntity.ok("OTP sent to " + phone); }
	 */

	@PostMapping("/sendOtp")
	@ResponseBody
	public String generateAndSendOtpOnMail(@RequestParam String email) {
		String otp = otpService.generateAndSendOtpOnMail(email);
		return otp;
	}

	@PostMapping("/verify")
	public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
		boolean isValid = otpService.verifyOtp(email, otp);
		return isValid ? ResponseEntity.ok("OTP verified")
				: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP");
	}

	// POST: Handle email input, send OTP
	@PostMapping("/requestOtp")
	public String sendOtpToEmail(@RequestParam("email") String email, HttpSession session, Model model) {
		otpService.generateAndSendOtpOnMail(email);
		session.setAttribute("otpEmail", email); // store for verification
		model.addAttribute("message", "OTP sent to " + email);
		return "verifyOTP";
	}

	// Show email input form
	@GetMapping("/requestOtp")
	public String showEmailForm() {
		return "requestOtp"; // show form to input email
	}

	// POST: Resend OTP
	@PostMapping("/resend")
	public String resendOtp(HttpSession session, Model model) {
		String email = (String) session.getAttribute("otpEmail");
		if (email != null) {
			String otp = otpService.generateAndSendOtpOnMail(email);
			model.addAttribute("successMessage", "New OTP sent to " + email);
		} else {
			model.addAttribute("errorMessage", "Session expired. Please enter your email again.");
			return "requestOtp";
		}
		return "verifyOTP";
	}

	@PostMapping("/otpVerification")
	public String handleOtpVerification(@RequestParam("otpInput") String otpInput, @RequestParam String action,
			HttpSession session, Model model) {

		String email = (String) session.getAttribute("otpEmail");

		if (action.equals("Verify OTP")) {
			boolean isValid = otpService.verifyOtp(email, otpInput);

			if (email == null) {
				model.addAttribute("errorMessage", "Session expired. Please request OTP again.");
				return "verifyOTP";
			}
			if (isValid) {
				model.addAttribute("successMessage", "OTP verified successfully.");
			} else {
				model.addAttribute("errorMessage", "Invalid or expired OTP");
			}

		} else if (action.equals("Resend OTP")) {
			if (email != null) {
				String otp = otpService.generateAndSendOtpOnMail(email);
				model.addAttribute("successMessage", "New OTP sent to " + email);
			} else {
				model.addAttribute("errorMessage", "Session expired. Please enter your email again.");
				return "requestOtp";
			}
		}
		return "verifyOTP";
	}

}
