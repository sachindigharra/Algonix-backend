package com.algonix.server.service.impl;

import com.algonix.server.dto.UpdateUserProfileRequest;
import com.algonix.server.dto.UserProfileResponse;
import com.algonix.server.entity.User;
import com.algonix.server.entity.UserProfile;
import com.algonix.server.exception.ResourceNotFoundException;
import com.algonix.server.mapper.UserMapper;
import com.algonix.server.repository.UserProfileRepository;
import com.algonix.server.repository.UserRepository;
import com.algonix.server.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserProfileRepository profileRepository;

    private final UserMapper userMapper;

    @Override
//    readOnly = true find out what is the use of readOnly in transactional and how it can improve performance
    @Transactional()
    public UserProfileResponse getUser(UUID userId) {

        log.info("Fetching user {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id : " + userId));

        return userMapper.mapToUserProfileResponse(user);
    }

    @Override
    public UserProfileResponse updateProfile(
            UUID userId,
            UpdateUserProfileRequest request) {

        log.info("Updating profile for user {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id : " + userId));

        UserProfile profile = user.getProfile();

        if (profile == null) {
            profile = new UserProfile();
            profile.setUser(user);
        }

        profile.setFullName(request.getFullName());
        profile.setAvatarUrl(request.getAvatarUrl());
        profile.setBio(request.getBio());

        profileRepository.save(profile);

        log.info("Profile updated successfully for user {}", userId);

        return userMapper.mapToUserProfileResponse(user);
    }

    @Override
    public void deleteUser(UUID userId) {

        log.warn("Deleting user {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id : " + userId));

        userRepository.delete(user);

        log.info("User deleted {}", userId);
    }




}
