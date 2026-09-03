package com.zest.productapi.service;

import com.zest.productapi.dto.UserRequestDto;
import com.zest.productapi.dto.UserResponseDto;
import com.zest.productapi.entity.User;
import com.zest.productapi.exception.UsernameAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.zest.productapi.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto registerUser(UserRequestDto userRequestDto) {

        if (userRepository.existsByUsername(userRequestDto.getUsername())) {
            throw new UsernameAlreadyExistsException(
                    "Username already exists: " + userRequestDto.getUsername()
            );
        }

        User user = new User();
        user.setUsername(userRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setRole("ADMIN");
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    private UserResponseDto mapToResponse(User user) {
        UserResponseDto response = new UserResponseDto();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        return response;
    }
}