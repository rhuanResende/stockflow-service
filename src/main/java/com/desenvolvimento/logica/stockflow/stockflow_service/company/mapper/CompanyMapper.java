package com.desenvolvimento.logica.stockflow.stockflow_service.company.mapper;

import com.desenvolvimento.logica.stockflow.stockflow_common.dto.CompanyResponse;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.dto.CompanyRequest;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.dto.CompanyUpdateRequest;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.entity.Company;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CompanyMapper {

    public CompanyResponse toDTO(Company company) {
        return new CompanyResponse(
                company.getId().toString(),
                company.getName().toUpperCase(),
                company.getDocument(),
                company.getEmail(),
                company.getPhone(),
                company.getActive() ? "ATIVO" : "INATIVO"
        );
    }

    public Company toEntity(CompanyRequest request) {
        Company company = new Company();
        company.setName(request.name().toUpperCase());
        company.setDocument(request.document());
        company.setEmail(request.email());
        company.setPhone(request.phone());
        return company;
    }

    public Company toEntity(Company company, CompanyUpdateRequest request) {
        company.setName(request.name().trim().toUpperCase());
        company.setDocument(request.document());
        company.setEmail(request.email());
        company.setPhone(request.phone());
        company.setUpdatedAt(LocalDateTime.now());
        return company;
    }
}
