package com.algonix.server.util;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.function.Function;

public interface JWTService {
    String generateToken(UserDetails user);

    String extractUsername(String token);

    boolean isTokenValid(String token, UserDetails user);

    java.util.Date extractExpiration(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimResolver);

    String extractRole(String token);

    Claims extractAllClaims(String token);

    SecretKey getKey();

    boolean isTokenExpired(String token);
}
