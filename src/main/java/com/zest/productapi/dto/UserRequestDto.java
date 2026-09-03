package com.zest.productapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRequestDto {

    @NotBlank(message = "Username is required")
    @Size(min = 6, max = 30, message = "Username must be between 6 and 30 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[_\\.])[A-Za-z\\d_\\.]+$",
            message = "Username must contain uppercase, lowercase, number and _ or ."
    )
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+$",
            message = "Password must contain uppercase, lowercase, number and special character"
    )
    private String password;
}
