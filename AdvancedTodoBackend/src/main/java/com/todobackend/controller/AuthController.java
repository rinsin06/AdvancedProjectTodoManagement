package com.todobackend.controller;

import org.apache.hc.client5.http.auth.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.todobackend.dto.AuthenticationResponse;
import com.todobackend.dto.LoginRequest;
import com.todobackend.entity.User;
import com.todobackend.service.CustomUserDetailsService;
import com.todobackend.service.UserService;
import com.todobackend.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	 @Autowired
	    private AuthenticationManager authenticationManager;

	    @Autowired
	    private JwtUtil jwtUtil;

	    @Autowired
	    private CustomUserDetailsService userDetailsService;
	    
	    @Autowired
	    private UserService userService;
	    
	    @Autowired
	    private PasswordEncoder passwordEncoder;

	    @PostMapping("/login")
	    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) throws InvalidCredentialsException {
	        try {
	            Authentication authentication = authenticationManager.authenticate(
	                    new UsernamePasswordAuthenticationToken(
	                            loginRequest.getUsername(), loginRequest.getPassword())
	            );

	            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
	            String jwtToken = jwtUtil.generateToken(userDetails);

	            return ResponseEntity.ok(new AuthenticationResponse(jwtToken));

	        } catch (BadCredentialsException e) {
	            throw new InvalidCredentialsException("Invalid username or password.");
	        }
	    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        userService.save(user);
        return ResponseEntity.ok("User registered successfully");
    }
}

