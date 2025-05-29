package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.bussinessObjects.Otp;
import com.example.demo.config.OtpUtil;
import com.example.demo.repository.OtpRepository;

@Service
public class OtpService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	OtpRepository otpRepository;

	@Transactional
	public String generateAndSendOtpOnMail(String email) {

		String otp = OtpUtil.generateOtp(6);
		LocalDateTime curruntTime = LocalDateTime.now();
		LocalDateTime expiry = curruntTime.plusMinutes(2);

		otpRepository.deleteByEmail(email);

		Otp otpEntity = new Otp();
		otpEntity.setEmail(email);
		otpEntity.setPhoneNumber("9999900000");
		otpEntity.setOtp(otp);
		otpEntity.setCreatedAt(curruntTime);
		otpEntity.setExpiresAt(expiry);

		otpRepository.save(otpEntity);

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		// message.setCc("abc@gmail.com");
		message.setSubject("Your OTP Code");
		message.setText("Your OTP is :" + otp);
		mailSender.send(message);
		return otp;
	}

	public boolean verifyOtp(String email, String inputOtp) {

		Optional<Otp> record = otpRepository.findByEmail(email);
		if (record.isPresent()) {
			Otp otp = record.get();
			if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
				return false;
			}

			boolean valid = otp.getOtp().equals(inputOtp);
			if (valid) {
				return valid;
			}
		}

		return false;
	}

}
