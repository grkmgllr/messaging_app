package com.megatron44.howudoin.controller;


import com.megatron44.howudoin.dto.JwtResponseDto;
import com.megatron44.howudoin.dto.LoginDto;
import com.megatron44.howudoin.dto.RegisterDto;
import com.megatron44.howudoin.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        authService.registerUser(registerDto);
        return ResponseEntity.ok("User registered successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto) {
        String token = authService.loginUser(loginDto);
        return ResponseEntity.ok(new JwtResponseDto(token));
    }
}

