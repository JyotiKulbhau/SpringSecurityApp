package com.example.demo.controller;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.service.UserService;

@Controller
@RequestMapping("/home")
public class HomeController {
	
	@Autowired
	private UserService userService;

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
    public String adminUser(Authentication authentication, Model model) {
        String uName = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));

        model.addAttribute("username", uName);
        model.addAttribute("role", role);
        model.addAttribute("users", userService.getAllUserData());

        return "admin_dashBoard";
    }

	@GetMapping("/public")
	public ResponseEntity<String> publicUser() {
		return ResponseEntity.ok("Public User");
	}

}
