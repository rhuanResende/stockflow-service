package com.desenvolvimento.logica.stockflow.stockflow_service.company.mapper;

import com.desenvolvimento.logica.stockflow.stockflow_common.dto.CompanyResponse;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.entity.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyResponse toDTO(Company company) {
        return new CompanyResponse(
                company.getId().toString(),
                company.getName().toUpperCase(),
                company.getDocument()
        );
    }
}
