package com.algonix.server.config;

import com.algonix.server.service.impl.CustomUserDetailsService;
import com.algonix.server.util.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Fetch Bearer Token from header

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extract the token
            log.info("Authorization header found, extracting token");
            String token = authHeader.substring(7);
            // Extract Username from token and validate it
            String username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource()
                            .buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
                log.info("Token validated successfully for username: {}", username);
            }
        }
        filterChain.doFilter(request, response);
    }
}
