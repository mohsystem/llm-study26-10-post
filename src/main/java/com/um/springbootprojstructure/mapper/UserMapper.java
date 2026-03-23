package com.um.springbootprojstructure.mapper;

import com.um.springbootprojstructure.dto.UserResponse;
import com.um.springbootprojstructure.entity.User;

public final class UserMapper {
    private UserMapper() {}

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}
