package com.desenvolvimento.logica.stockflow.stockflow_service.company.service;

import com.desenvolvimento.logica.stockflow.stockflow_common.dto.CompanyResponse;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.entity.Company;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.mapper.CompanyMapper;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CompanyService {

    private final CompanyMapper companyMapper;
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyMapper companyMapper, CompanyRepository companyRepository) {
        this.companyMapper = companyMapper;
        this.companyRepository = companyRepository;
    }

    public CompanyResponse findById(String id) {
        Company company = companyRepository
                .findByIdAndDeletedFalse(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Company não encontrada."));
        return companyMapper.toDTO(company);
    }

    public CompanyResponse findByName(String name) {
        Company company = companyRepository
                .findCompanyByNameAndDeletedFalse(name)
                .orElseThrow(() -> new RuntimeException("Company não encontrada."));
        return companyMapper.toDTO(company);
    }
}
