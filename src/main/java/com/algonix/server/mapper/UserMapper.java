package com.algonix.server.mapper;

import com.algonix.server.dto.response.UserProfileResponse;
import com.algonix.server.entity.User;
import com.algonix.server.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserProfileResponse mapToUserProfileResponse(User user) {

        UserProfile profile = user.getProfile();

        return UserProfileResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName() != null ? user.getFullName() : null)
                .avatarUrl(profile != null ? profile.getAvatarUrl() : null)
                .bio(profile != null ? profile.getBio() : null)
                .build();
    }
}
