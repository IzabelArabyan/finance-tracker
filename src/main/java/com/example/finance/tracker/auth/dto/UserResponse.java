package com.example.finance.tracker.auth.dto;

import com.example.finance.tracker.user.User;

import java.time.Instant;

public record UserResponse(Long id, String username, Instant createdAt) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getCreatedAt());
    }
}
