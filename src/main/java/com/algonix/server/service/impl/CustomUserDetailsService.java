package com.algonix.server.service.impl;

import com.algonix.server.entity.User;
import com.algonix.server.repository.UserRepository;
import com.algonix.server.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CustomUserPrincipal loadUserByUsername(String username) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found: " + username
                        ));

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(
                        user.getRole().name()
                )
        );

        return new CustomUserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                authorities
        );
    }
}
