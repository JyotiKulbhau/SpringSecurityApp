package com.example.demo.config;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OtpUtil {
	
	public static Logger logger=LoggerFactory.getLogger(OtpUtil.class);
	
	public static String generateOtp(int length) {
		StringBuilder otp=new StringBuilder();
		String numbers="4560123789";
		Random random=new Random();
		
		for(int i=1;i<=length;i++) {
			int randomIndex=random.nextInt(numbers.length());
			otp.append(numbers.charAt(randomIndex));
		}
		System.out.println("otp : "+otp);
		return otp.toString();
	}
	

	public static void main(String[] args) {
		generateOtp(6);
	}
}
