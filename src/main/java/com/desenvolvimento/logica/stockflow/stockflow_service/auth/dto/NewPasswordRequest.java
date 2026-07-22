package com.desenvolvimento.logica.stockflow.stockflow_service.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record NewPasswordRequest(
        @NotBlank
        String currentPassword,
        @NotBlank
        String newPassword,
        @NotBlank
        String confirmPassword
) {
}
