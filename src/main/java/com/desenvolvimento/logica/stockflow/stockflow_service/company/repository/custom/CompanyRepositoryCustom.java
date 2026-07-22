package com.desenvolvimento.logica.stockflow.stockflow_service.company.repository.custom;

import com.desenvolvimento.logica.stockflow.stockflow_service.company.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompanyRepositoryCustom {

    Page<Company> filter(
            final String name,
            final String document,
            final String status,
            final Pageable pageable
    );

    List<Company> queryCompanyByFilterAndDeletedFalse(final String filter);
}
