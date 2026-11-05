package com.example.finance.tracker.category.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank(message = "name must not be blank")
        String name
) {
}
