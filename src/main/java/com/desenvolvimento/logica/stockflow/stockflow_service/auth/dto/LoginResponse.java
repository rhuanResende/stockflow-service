package com.desenvolvimento.logica.stockflow.stockflow_service.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        long expiresIn
) {
}
