package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.bussinessObjects.Otp;

public interface OtpRepository extends JpaRepository<Otp,Integer> {

	Optional<Otp> findByEmail(String email);
	void deleteByEmail(String email);
}
