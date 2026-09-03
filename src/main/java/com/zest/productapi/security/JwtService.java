package com.zest.productapi.security;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(String username, String role) {

        return JWT.create()
                .withSubject(username)
                .withClaim("role", role)
                .withIssuedAt(new Date())
                .withExpiresAt(
                        new Date(
                                System.currentTimeMillis() + expiration
                        )
                )
                .sign(Algorithm.HMAC256(secretKey));
    }

    public String extractUsername(String token) {

        return JWT.require(Algorithm.HMAC256(secretKey))
                .build()
                .verify(token)
                .getSubject();
    }

    public String extractRole(String token) {

        return JWT.require(Algorithm.HMAC256(secretKey))
                .build()
                .verify(token)
                .getClaim("role")
                .asString();
    }

    public boolean validateToken(String token) {

        try {

            JWT.require(Algorithm.HMAC256(secretKey))
                    .build()
                    .verify(token);

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}