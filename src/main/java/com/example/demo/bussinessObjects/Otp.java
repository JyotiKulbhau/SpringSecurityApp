package com.example.demo.bussinessObjects;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="otp_verification")
@Getter
@Setter
public class Otp {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private String phoneNumber; 
	private String email;
	private String otp;
	private LocalDateTime createdAt;
	private LocalDateTime expiresAt;
	

}
