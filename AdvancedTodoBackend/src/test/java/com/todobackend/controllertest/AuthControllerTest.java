package com.todobackend.controllertest;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.apache.hc.client5.http.auth.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.todobackend.controller.AuthController;
import com.todobackend.dto.AuthenticationResponse;
import com.todobackend.dto.LoginRequest;
import com.todobackend.entity.User;
import com.todobackend.service.CustomUserDetailsService;
import com.todobackend.service.UserService;
import com.todobackend.util.JwtUtil;

class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticateUser_ShouldReturnToken_WhenCredentialsAreValid() throws InvalidCredentialsException {
       
        LoginRequest loginRequest = new LoginRequest("testuser", "password");
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("mocked-jwt-token");

        
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthenticationResponse authResponse = (AuthenticationResponse) response.getBody();
        assertEquals("mocked-jwt-token", authResponse.getToken());
    }

    @Test
    void authenticateUser_ShouldThrowInvalidCredentialsException_WhenCredentialsAreInvalid() {
        
        LoginRequest loginRequest = new LoginRequest("testuser", "wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));

       
        try {
            authController.authenticateUser(loginRequest);
        } catch (InvalidCredentialsException e) {
            assertEquals("Invalid username or password.", e.getMessage());
        }
    }

    @Test
    void signup_ShouldReturnSuccessMessage_WhenUserIsRegistered() {
        
        User user = new User();
        user.setUsername("newuser");
        user.setPassword("newpassword");

        when(userService.save(any(User.class))).thenReturn(user);

        
        ResponseEntity<String> response = authController.signup(user);

        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully", response.getBody());
    }
}

