package com.example.demo.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public static Logger logger=LoggerFactory.getLogger(EmailService.class);
	@Autowired
	private JavaMailSender mailSender;

	public void sendPasswordResetEmail(String to, String subject, String message) {

		logger.debug("sending mail....");
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(to);
		mail.setSubject(subject);
		mail.setText(message);
		mail.setCc("abc@gmail.com","pqr@gmail.com");
		mail.setSentDate(new Date());

		mailSender.send(mail);
		logger.debug("mail sent");

	}

}
