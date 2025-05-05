package com.example.demo.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // This enables @PreAuthorize and @PostAuthorize
public class SecurityConfig {

	// Inject the properties from application.properties
	/*
	 * @Value("${spring.security.user.name}") private String adminUsername;
	 * 
	 * @Value("${spring.security.user.password}") private String adminPassword;
	 * 
	 * @Value("${spring.security.user.roles}") private String adminRoles;
	 * 
	 * @Value("${app.security.user.name}") private String normalUsername;
	 * 
	 * @Value("${app.security.user.password}") private String normalPassword;
	 * 
	 * @Value("${app.security.user.roles}") private String normalRoles;
	 */

	/*
	 * @Bean public SecurityFilterChain filterChain(HttpSecurity http) throws
	 * Exception { http.csrf(csrf -> csrf.disable()) // disable for simplicity
	 * .authorizeHttpRequests(auth ->
	 * auth.requestMatchers("/home/public").permitAll() // no need to configure
	 * because of @PreAuthorize and @PostAuthorize
	 * 
	 * .requestMatchers("/home/normal").hasRole("NORMAL").requestMatchers(
	 * "/home/admin") .hasRole("ADMIN")
	 * 
	 * .anyRequest().authenticated()) .formLogin(form ->
	 * form.permitAll()).logout(logout -> logout.logoutUrl("/home/logout") //
	 * optional // (default) .logoutSuccessUrl("/home/public") // where to go after
	 * logout .permitAll());
	 * 
	 * return http.build(); }
	 */

	// In Memory user details
	/*
	 * @Bean public UserDetailsService userDetailsService() { UserDetails normalUser
	 * = User.withUsername(normalUsername).password(passwordEncoder().encode(
	 * normalPassword)) .roles(normalRoles.split(",")).build();
	 * 
	 * UserDetails admin =
	 * User.withUsername(adminUsername).password(passwordEncoder().encode(
	 * adminPassword)) .roles(adminRoles.split(",")).build();
	 * 
	 * return new InMemoryUserDetailsManager(normalUser, admin); }
	 */

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth.requestMatchers("/home/public", "/forgotPass", "/resetPass").permitAll()
				.anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login")
						.successHandler((request, response, authentication) -> {
							boolean isAdmin = authentication.getAuthorities().stream()
									.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

							boolean isUser = authentication.getAuthorities().stream()
									.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));

							if (isAdmin) {
								response.sendRedirect("/home/admin");
							} else if (isUser) {
								response.sendRedirect("/home/user");
							} else {
								response.sendRedirect("/home/public");
							}
						}).failureUrl("/login?error=true").permitAll())
				.logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/home/logout", "GET"))
						.logoutSuccessUrl("/login?logout").permitAll());

		return http.build();
	}

	// Fetch user details from database
	@Bean
	public UserDetailsService userDetailsService(DataSource dataSource) {
		// JdbcUserDetailsManager uses the default Spring Security schema
		JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
		System.out.println("userDetailsService getJdbcTemplate:" + userDetailsManager.getJdbcTemplate());

		return userDetailsManager;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
