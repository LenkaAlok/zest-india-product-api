package com.zest.productapi.service;
import com.zest.productapi.dto.LoginRequestDto;
import com.zest.productapi.dto.LoginResponseDto;
import com.zest.productapi.dto.RefreshTokenRequestDto;
import com.zest.productapi.entity.RefreshToken;
import com.zest.productapi.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zest.productapi.repository.RefreshTokenRepository;
import com.zest.productapi.repository.UserRepository;
import com.zest.productapi.security.JwtService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    // LOGIN
    public LoginResponseDto login(LoginRequestDto request) {

        // Check username and password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Get user from database
        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // Generate access token
        String accessToken = jwtService.generateToken(
                user.getUsername(),
                user.getRole()
        );

        // Generate refresh token
        String refreshToken = createRefreshToken(user);

        LoginResponseDto response = new LoginResponseDto();

        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");

        return response;
    }

    // REFRESH TOKEN
    @Transactional
    public LoginResponseDto refreshToken(
            RefreshTokenRequestDto request) {

        // Find refresh token
        RefreshToken oldToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid refresh token"
                        )
                );

        // Check if already used
        if (oldToken.isRevoked()) {
            throw new RuntimeException(
                    "Refresh token already used"
            );
        }

        // Check expiration
        if (oldToken.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Refresh token expired"
            );
        }

        // Revoke old refresh token
        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);

        // Get user
        User user = oldToken.getUser();

        // Create new access token
        String accessToken = jwtService.generateToken(
                user.getUsername(),
                user.getRole()
        );

        // Create new refresh token
        String newRefreshToken = createRefreshToken(user);

        LoginResponseDto response = new LoginResponseDto();

        response.setAccessToken(accessToken);
        response.setRefreshToken(newRefreshToken);
        response.setTokenType("Bearer");

        return response;
    }

    // CREATE REFRESH TOKEN
    private String createRefreshToken(User user) {

        String token = UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(
                LocalDateTime.now().plusDays(7)
        );
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);

        return token;
    }
}