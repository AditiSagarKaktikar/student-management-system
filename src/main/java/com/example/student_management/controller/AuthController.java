package com.example.student_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.student_management.entity.User;
import com.example.student_management.repository.UserRepository;
import com.example.student_management.security.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
@Autowired
private UserRepository userRepository;
@Autowired
private PasswordEncoder passwordEncoder;

@PostMapping("/register")
public String register(@RequestBody User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword())); // hash BEFORE saving
    userRepository.save(user);
    return "User registered successfully";
}
@Autowired
private AuthenticationManager authenticationManager;

@Autowired
private JwtUtil jwtUtil;

@PostMapping("/login")
public String login(@RequestBody User loginRequest) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(), loginRequest.getPassword())
    );

    // Fetch the real user from DB to get their actual role
    User user = userRepository.findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

    return jwtUtil.generateToken(user.getUsername(), user.getRole().name());
}
//for test purpose

//@Autowired
//private UserRepository userRepository;   // you likely already have this one, don't duplicate it


	
	

}
