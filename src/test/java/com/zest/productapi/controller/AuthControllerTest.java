package com.zest.productapi.controller;
import com.zest.productapi.dto.LoginRequestDto;
import com.zest.productapi.dto.LoginResponseDto;
import com.zest.productapi.dto.RefreshTokenRequestDto;
import com.zest.productapi.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {

        AuthController authController =
                new AuthController(authService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();
    }
    // 1. LOGIN

    @Test
    void testLogin() throws Exception {
        LoginResponseDto response =
                new LoginResponseDto();

        response.setAccessToken("access-token");
        response.setRefreshToken("refresh-token");
        response.setTokenType("Bearer");
        when(authService.login(
                any(LoginRequestDto.class)
        )).thenReturn(response);
        String requestJson = """
                {
                    "username": "Admin_123",
                    "password": "Admin@123"
                }
                """;


        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.accessToken")
                                .value("access-token")
                )
                .andExpect(
                        jsonPath("$.refreshToken")
                                .value("refresh-token")
                )
                .andExpect(
                        jsonPath("$.tokenType")
                                .value("Bearer")
                );


        // Verify service was called

        verify(authService, times(1))
                .login(any(LoginRequestDto.class));
    }
        // 2. REFRESH TOKEN

    @Test
    void testRefreshToken() throws Exception {
        LoginResponseDto response =
                new LoginResponseDto();

        response.setAccessToken("new-access-token");
        response.setRefreshToken("new-refresh-token");
        response.setTokenType("Bearer");
        when(authService.refreshToken(
                any(RefreshTokenRequestDto.class)
        )).thenReturn(response);
        String requestJson = """
                {
                    "refreshToken": "old-refresh-token"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/refresh")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.accessToken")
                                .value("new-access-token")
                )
                .andExpect(
                        jsonPath("$.refreshToken")
                                .value("new-refresh-token")
                )
                .andExpect(
                        jsonPath("$.tokenType")
                                .value("Bearer")
                );
        verify(authService, times(1))
                .refreshToken(
                        any(RefreshTokenRequestDto.class)
                );
    }
}