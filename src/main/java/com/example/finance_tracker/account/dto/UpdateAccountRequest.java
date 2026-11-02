package com.example.finance_tracker.account.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateAccountRequest(
        @NotBlank(message = "name must not be blank")
        String name
) {

}
