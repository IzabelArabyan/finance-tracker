package com.example.finance.tracker.account.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateAccountRequest(
        @NotBlank(message = "name must not be blank")
        String name
) {

}
