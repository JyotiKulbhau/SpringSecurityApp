package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity //  This enables @PreAuthorize and @PostAuthorize
public class SecurityConfig {

	// Inject the properties
	@Value("${spring.security.user.name}")
	private String adminUsername;

	@Value("${spring.security.user.password}")
	private String adminPassword;

	@Value("${spring.security.user.roles}")
	private String adminRoles;

	@Value("${app.security.user.name}")
	private String normalUsername;

	@Value("${app.security.user.password}")
	private String normalPassword;

	@Value("${app.security.user.roles}")
	private String normalRoles;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // disable for simplicity
				.authorizeHttpRequests(auth -> auth.requestMatchers("/home/public").permitAll()
						//no need to configure because of  @PreAuthorize and @PostAuthorize
						/*
						 * .requestMatchers("/home/normal").hasRole("NORMAL").requestMatchers(
						 * "/home/admin") .hasRole("ADMIN")
						 */
						.anyRequest().authenticated())
				.formLogin(form -> form.permitAll()).logout(logout -> logout.logoutUrl("/logout") // optional (default)
						.logoutSuccessUrl("/home/public") // where to go after logout
						.permitAll());

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails normalUser = User.withUsername(normalUsername).password(passwordEncoder().encode(normalPassword))
				.roles(normalRoles.split(",")).build();

		UserDetails admin = User.withUsername(adminUsername).password(passwordEncoder().encode(adminPassword))
				.roles(adminRoles.split(",")).build();

		return new InMemoryUserDetailsManager(normalUser, admin);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
