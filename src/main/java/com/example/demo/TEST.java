package com.example.demo;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TEST {

	public static void getEncodedPass() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println(encoder.encode("john123")); // $2a$10$HYZUv18Og6jhyz5L4xCFvOv12ZB/Lc7hNBiOmX4Hp6FbYVmfKp6tm
	}
	
	public class PasswordCheck {
	    public static void main(String[] args) {
	        String rawPassword     = "admin123";  
	        String storedHash      = "$2a$10$HPdyCyCo0YlXIPViaf8H1esEQIEf1r7RExyksrVnug4iFSigV1T/q";
	        
	        PasswordEncoder encoder = new BCryptPasswordEncoder();
	        boolean matches         = encoder.matches(rawPassword, storedHash);
	        
	        System.out.println("Password matches? " + matches);
	    }
	}

	public static void main(String[] args) {
		//getEncodedPass();
	}

}
