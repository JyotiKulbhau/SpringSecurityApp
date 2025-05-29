package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.OtpService;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@RestController 
@RequestMapping("/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @Value("${twilio.phone.number}")
    private String twilioNumber;

	/*
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

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        boolean isValid = otpService.verifyOtp(email, otp);
        return isValid ?
                ResponseEntity.ok("OTP verified") :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP");
    }
}
