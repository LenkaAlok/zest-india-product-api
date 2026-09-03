package com.zest.productapi.controller;
import com.zest.productapi.dto.UserRequestDto;
import com.zest.productapi.dto.UserResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.zest.productapi.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(
            @Valid @RequestBody UserRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.registerUser(request));
    }
}