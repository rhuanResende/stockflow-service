package com.desenvolvimento.logica.stockflow.stockflow_service.company.repository;

import com.desenvolvimento.logica.stockflow.stockflow_service.company.entity.Company;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.repository.custom.CompanyRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID>, CompanyRepositoryCustom {
    Optional<Company> findCompanyByDocumentAndActiveTrue(String document);
    Optional<Company> findCompanyByIdAndActiveTrue(UUID id);
    Optional<Company> findCompanyByIdAndActiveFalse(UUID id);
}
