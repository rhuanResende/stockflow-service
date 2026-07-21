package com.desenvolvimento.logica.stockflow.stockflow_service.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        String document,

        @NotBlank
        String password
) {
}
