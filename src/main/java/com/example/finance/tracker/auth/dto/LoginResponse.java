package com.example.finance.tracker.auth.dto;

public record LoginResponse(String accessToken, String refreshToken, String tokenType, long expiresIn) {
}