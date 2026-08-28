package com.algonix.server.util;

import com.algonix.server.security.CustomUserPrincipal;
import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;
import java.util.function.Function;

public interface JWTService {
    String generateToken(CustomUserPrincipal  user);

    String extractUsername(String token);

    boolean isTokenValid(String token, CustomUserPrincipal  user);

    java.util.Date extractExpiration(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimResolver);

    String extractRole(String token);

    Claims extractAllClaims(String token);

    SecretKey getKey();

    boolean isTokenExpired(String token);
}
