package com.desenvolvimento.logica.stockflow.stockflow_service.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record UserCreateRequest(

        @NotNull(message = "Empresa é obrigatório")
        UUID companyId,

        @NotNull(message = "Perfil é obrigatório")
        UUID profileId,

        @NotBlank(message = "Nome é obrigatorio")
        String name,

        @NotBlank(message = "Documento é obrigatório")
        @Pattern(
                regexp = "^[A-Za-z0-9]{11}$",
                message = "Documento deve conter 11 caracteres"
        )
        String document,

        @NotBlank(message = "Email é obrigatório")
        @Email
        String email,

        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(
                regexp = "^\\d{11}$",
                message = "Telefone deve conter 11 dígitos"
        )
        String phone
) {
}
