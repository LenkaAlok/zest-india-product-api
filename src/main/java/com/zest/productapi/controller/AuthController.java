package com.zest.productapi.controller;
import com.zest.productapi.dto.LoginRequestDto;
import com.zest.productapi.dto.LoginResponseDto;
import com.zest.productapi.dto.RefreshTokenRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.zest.productapi.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto request) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDto request) {

        return ResponseEntity.ok(
                authService.refreshToken(request)
        );
    }
}