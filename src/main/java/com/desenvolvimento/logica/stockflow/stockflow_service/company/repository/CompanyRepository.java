package com.desenvolvimento.logica.stockflow.stockflow_service.company.repository;

import com.desenvolvimento.logica.stockflow.stockflow_service.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Optional<Company> findCompanyByNameAndDeletedFalse(String name);
    Optional<Company> findByIdAndDeletedFalse(UUID id);
}
