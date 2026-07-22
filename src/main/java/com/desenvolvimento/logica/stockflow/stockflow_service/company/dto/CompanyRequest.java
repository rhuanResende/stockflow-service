package com.desenvolvimento.logica.stockflow.stockflow_service.company.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CompanyRequest(
        @NotBlank(message = "Nome é obrigatorio")
        String name,

        @NotBlank(message = "Documento é obrigatório")
        @Pattern(
                regexp = "^[A-Za-z0-9]{14}$",
                message = "CNPJ deve conter 14 caracteres alfanuméricos"
        )
        String document,

        @NotBlank(message = "Email é obrigatório")
        @Email()
        String email,

        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(
                regexp = "^\\d{10,11}$",
                message = "Telefone deve conter 10 ou 11 dígitos"
        )
        String phone

) {
}
