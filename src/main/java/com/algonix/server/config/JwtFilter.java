package com.algonix.server.config;

import com.algonix.server.security.CustomUserPrincipal;
import com.algonix.server.service.impl.CustomUserDetailsService;
import com.algonix.server.util.JWTService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Configuration
@EnableWebSecurity
@AllArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter{
    private JWTService jwtService;

    private CustomUserDetailsService userDetailsService;

    private final ApplicationContext context;


    /*
        this filter will intercept incoming HTTP requests and check for the presence of a JWT token in the Authorization header.
        If a token is found, it can be validated and processed to authenticate the user before allowing access to protected resources.
        Fallback: If Authorization header is not present, check for JWT in cookies.
    */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;

        // Try Authorization header first
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            log.info("Authorization header found, extracting token");
        } else {
            // Fall back to cookie
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) {
                        token = cookie.getValue();
                        log.info("JWT token found in cookies");
                        break;
                    }
                }
            }
        }

        // If token found, validate it
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Check if token is expired
                if (jwtService.isTokenExpired(token)) {
                    log.warn("JWT token is expired");
                } else {
                    // Extract Username from token and validate it
                    String username = jwtService.extractUsername(token);

                    if (username != null) {
                        CustomUserPrincipal userDetails = userDetailsService.loadUserByUsername(username);
                        if (jwtService.isTokenValid(token, userDetails)) {
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            authToken.setDetails(new WebAuthenticationDetailsSource()
                                    .buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                            log.info("Token validated successfully for username: {}", username);
                        }
                    }
                }
            } catch (ExpiredJwtException e) {

                // JWTService already logs the parsing failure.
                // Do not authenticate the request.

                SecurityContextHolder.clearContext();

            } catch (JwtException e) {

                SecurityContextHolder.clearContext();

            } catch (Exception e) {

                log.error("Unexpected error while processing JWT", e);
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
