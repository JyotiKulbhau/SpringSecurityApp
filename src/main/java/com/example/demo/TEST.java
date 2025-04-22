package com.example.demo;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TEST {

	public static void getEncodedPass() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println(encoder.encode("john123")); // $2a$10$HYZUv18Og6jhyz5L4xCFvOv12ZB/Lc7hNBiOmX4Hp6FbYVmfKp6tm
	}

	public static void main(String[] args) {
		getEncodedPass();
	}

}
