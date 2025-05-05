package com.example.demo.controller;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/user")
	public ResponseEntity<String> normalUser(Authentication authentication) {
		String username = authentication.getName();
		String role = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(", "));
		return ResponseEntity.ok("Hi " + username + ", your role is :" + role);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/admin")
	public ResponseEntity<String> adminUser(Authentication authentication) {
		String uName = authentication.getName();
		String role = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(", "));
		return ResponseEntity.ok("Hi " + uName + ", your role is :" + role);
	}

	@GetMapping("/public")
	public ResponseEntity<String> publicUser() {
		return ResponseEntity.ok("Public User");
	}

}
