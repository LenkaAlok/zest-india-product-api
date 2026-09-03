package com.zest.productapi.config;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.zest.productapi.security.JwtFilter;
import org.springframework.security.authentication.AuthenticationManager;
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> {})

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        ))

                .authorizeHttpRequests(auth -> auth

                        // Public APIs
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/users/register",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // USER + ADMIN can GET
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/v1/products/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // Only ADMIN can create
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/v1/products"
                        ).hasRole("ADMIN")

                        // Only ADMIN can update
                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/api/v1/products/**"
                        ).hasRole("ADMIN")

                        // Only ADMIN can delete
                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/api/v1/products/**"
                        ).hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
    // AuthenticationManager for AuthService
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }
}