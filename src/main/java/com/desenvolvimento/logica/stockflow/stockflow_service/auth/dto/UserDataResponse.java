package com.desenvolvimento.logica.stockflow.stockflow_service.auth.dto;

import com.desenvolvimento.logica.stockflow.stockflow_common.dto.CompanyResponse;

import java.util.List;

public record UserDataResponse(
        String id,
        String name,
        String document,
        String email,
        String phone,
        CompanyResponse company,
        List<String> roles
) {
}
