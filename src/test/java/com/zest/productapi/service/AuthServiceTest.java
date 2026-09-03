package com.zest.productapi.service;
import com.zest.productapi.dto.LoginRequestDto;
import com.zest.productapi.dto.LoginResponseDto;
import com.zest.productapi.dto.RefreshTokenRequestDto;
import com.zest.productapi.entity.RefreshToken;
import com.zest.productapi.entity.User;
import com.zest.productapi.repository.RefreshTokenRepository;
import com.zest.productapi.repository.UserRepository;
import com.zest.productapi.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;


    // =====================================================
    // LOGIN SUCCESS
    // =====================================================

    @Test
    void testLoginSuccess() {

        // Arrange
        LoginRequestDto request = new LoginRequestDto();

        request.setUsername("Admin_123");
        request.setPassword("Admin@123");


        User user = new User();

        user.setUsername("Admin_123");
        user.setRole("ADMIN");


        LoginResponseDto expectedResponse = new LoginResponseDto();

        expectedResponse.setAccessToken("access-token");
        expectedResponse.setRefreshToken("refresh-token");
        expectedResponse.setTokenType("Bearer");


        when(userRepository.findByUsername("Admin_123"))
                .thenReturn(Optional.of(user));


        when(jwtService.generateToken(
                "Admin_123",
                "ADMIN"
        )).thenReturn("access-token");


        // createRefreshToken() inside AuthService
        // saves a RefreshToken and returns its token.
        RefreshToken savedRefreshToken = new RefreshToken();

        when(refreshTokenRepository.save(
                any(RefreshToken.class)
        )).thenReturn(savedRefreshToken);


        // Act
        LoginResponseDto actualResponse =
                authService.login(request);


        // Assert
        assertNotNull(actualResponse);

        assertEquals(
                "access-token",
                actualResponse.getAccessToken()
        );

        assertNotNull(
                actualResponse.getRefreshToken()
        );

        assertEquals(
                "Bearer",
                actualResponse.getTokenType()
        );


        // Verify authentication was performed
        verify(authenticationManager, times(1))
                .authenticate(
                        any(UsernamePasswordAuthenticationToken.class)
                );


        // Verify user was searched
        verify(userRepository, times(1))
                .findByUsername("Admin_123");


        // Verify JWT was generated
        verify(jwtService, times(1))
                .generateToken(
                        "Admin_123",
                        "ADMIN"
                );


        // Verify refresh token was saved
        verify(refreshTokenRepository, times(1))
                .save(any(RefreshToken.class));
    }


    // =====================================================
    // LOGIN - USER NOT FOUND
    // =====================================================

    @Test
    void testLoginUserNotFound() {

        // Arrange
        LoginRequestDto request = new LoginRequestDto();

        request.setUsername("UnknownUser");
        request.setPassword("Password@123");


        when(userRepository.findByUsername("UnknownUser"))
                .thenReturn(Optional.empty());


        // Act & Assert
        assertThrows(
                RuntimeException.class,
                () -> authService.login(request)
        );


        verify(userRepository, times(1))
                .findByUsername("UnknownUser");
    }


    // =====================================================
    // REFRESH TOKEN - REVOKED TOKEN
    // =====================================================

    @Test
    void testRefreshTokenRevoked() {

        // Arrange
        RefreshTokenRequestDto request =
                new RefreshTokenRequestDto();

        request.setRefreshToken("old-refresh-token");


        RefreshToken oldToken =
                org.mockito.Mockito.mock(RefreshToken.class);


        when(refreshTokenRepository.findByToken(
                "old-refresh-token"
        )).thenReturn(Optional.of(oldToken));


        when(oldToken.isRevoked())
                .thenReturn(true);


        // Act & Assert
        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.refreshToken(request)
                );


        assertEquals(
                "Refresh token already used",
                exception.getMessage()
        );


        verify(refreshTokenRepository, times(1))
                .findByToken("old-refresh-token");
    }


    // =====================================================
    // REFRESH TOKEN - TOKEN NOT FOUND
    // =====================================================

    @Test
    void testRefreshTokenNotFound() {

        // Arrange
        RefreshTokenRequestDto request =
                new RefreshTokenRequestDto();

        request.setRefreshToken("invalid-token");


        when(refreshTokenRepository.findByToken(
                "invalid-token"
        )).thenReturn(Optional.empty());


        // Act & Assert
        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.refreshToken(request)
                );


        assertEquals(
                "Invalid refresh token",
                exception.getMessage()
        );


        verify(refreshTokenRepository, times(1))
                .findByToken("invalid-token");
    }
}