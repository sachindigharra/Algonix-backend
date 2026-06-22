package com.algonix.server.util.impl;

import com.algonix.server.util.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Slf4j
@Service
public class JWTServiceImpl implements JWTService {


    // Note: Replace with actual secure key in production .ONLY Testing purpose
    private final String SECRET_KEY = "MzJieXRlc2xvbmdzZWNyZXRrZXltYW51YWxseWVuY29kZWQ=";
    @Override
    public String generateToken(UserDetails user) {
        log.info("Generating JWT for username: {}", user.getUsername());
        // role of the user
        log.debug("User role for token: {}", user.getAuthorities());
        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", user.getAuthorities());
            String token = Jwts.builder()
                    // adding claims to the token, including the user's role
                    .claims()
                    .add(claims)
                    .subject(user.getUsername())
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour expiration
                    .and()
                    .signWith(getKey())
                    .compact();

            log.info("JWT generated successfully for username: {}", user.getUsername());
            log.debug("Generated token (truncated): {}", token.substring(0, Math.min(token.length(), 20)) + "...");
            return token;
        } catch (Exception e) {
            log.error("Failed to generate JWT for username: {}. Error: {}", user.getUsername(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String extractUsername(String token) {
        log.info("Extracting username from token");
        try {
            // subject is the username in JWT, so we extract it using the extractClaim method
            String username = extractClaim(token, Claims::getSubject);
            log.debug("Extracted username: {}", username);
            return username;
        } catch (Exception e) {
            log.error("Failed to extract username from token. Error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean isTokenValid(String token, UserDetails user) {
        log.info("Validating token for user: {}", user.getUsername());
        try {
            // extract the username from the token and compare it with the provided user's username
            final String username = extractUsername(token);
            boolean isValid = (username.equals(user.getUsername()));
            log.debug("Token validation result for user {}: {}", user.getUsername(), isValid);
            return isValid;
        } catch (Exception e) {
            log.error("Failed to validate token for user {}. Error: {}", user.getUsername(), e.getMessage(), e);
        }
        return false;
    }

    @Override
    public Date extractExpiration(String token) {
       log.info("Extracting expiration date from token");
        try {
            // The expiration date is stored in the claims, so we extract it using the extractClaim method
            Date expiration = extractClaim(token,Claims::getExpiration);
            log.debug("Extracted expiration date: {}", expiration);
            return expiration;
        } catch (Exception e) {
            log.error("Failed to extract expiration date from token. Error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        log.debug("Extracting claim from token");
        try {
            final Claims claims = extractAllClaims(token);
            // Apply the provided claim resolver function to extract the specific claim
            return claimResolver.apply(claims);
        } catch (Exception e) {
            log.error("Failed to extract claim from token. Error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String extractRole(String token) {
        log.info("Extracting role from token");
        try {
            // The role is stored in the claims under the key "role", so we extract it using the extractClaim method
            String role = extractClaim(token,claims->claims.get("role", String.class));
            log.debug("Extracted role: {}", role);
            return role;
        } catch (Exception e) {
            log.error("Failed to extract role from token. Error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Claims extractAllClaims(String token) {
        log.debug("Parsing all claims from token");
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Failed to parse claims from token. Error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public SecretKey getKey() {
        log.debug("Retrieving signing key");
        try {
            // Decode the base64-encoded secret key and create a SecretKey object for signing and verifying JWTs
            byte[]keyBytes = Decoders.BASE64.decode(SECRET_KEY);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            log.error("Failed to decode secret key. Error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean isTokenExpired(String token){
        log.info("checking if token is expired");
        try{
            // Extract the expiration date from the token and check if it is before the current date
            boolean isExpired = extractExpiration(token).before(new Date());
            log.debug("Token expiration status: {}", isExpired);
            return isExpired;
        }
        catch (Exception e){
            log.error("Failed to check if token is expired. Error: {}", e.getMessage(), e);
            throw e;
        }
    }
    // Implement the methods defined in the JWTService interface here
}
