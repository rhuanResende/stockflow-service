package com.desenvolvimento.logica.stockflow.stockflow_service.company.service;

import com.desenvolvimento.logica.stockflow.stockflow_common.dto.CompanyResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.exception.BusinessException;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.dto.CompanyRequest;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.dto.CompanyUpdateRequest;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.entity.Company;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.mapper.CompanyMapper;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.repository.CompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CompanyService {

    private final CompanyMapper companyMapper;
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyMapper companyMapper, CompanyRepository companyRepository) {
        this.companyMapper = companyMapper;
        this.companyRepository = companyRepository;
    }

    public CompanyResponse create(CompanyRequest request) {

        var existingCompany = companyRepository
                .findCompanyByDocumentAndActiveTrue(request.document());
        if (existingCompany.isPresent()) {
            throw new BusinessException("Já existe uma empresa cadastrada com este documento.");
        }
        Company company = companyMapper.toEntity(request);
        companyRepository.save(company);
        return companyMapper.toDTO(company);
    }

    public CompanyResponse findCompanyById(UUID id) {
        Company company = companyRepository
                .findCompanyByIdAndActiveTrue(id)
                .orElseThrow(() -> new BusinessException("Empresa não encontrada."));
        return companyMapper.toDTO(company);
    }

    public Page<CompanyResponse> getCompanies(
            final String name,
            final String document,
            final String status,
            final Pageable pageable) {

        Page<Company> page = companyRepository.filter(
                name,
                document,
                status,
                pageable
        );
        return page.map(companyMapper::toDTO);
    }

    public CompanyResponse update(CompanyUpdateRequest request) {
        Optional<Company> companyByDocument =
                companyRepository.findCompanyByDocumentAndActiveTrue(
                        request.document()
                );

        if (companyByDocument.isPresent()
                && !companyByDocument.get().getId().equals(request.id())) {

            throw new BusinessException("Já existe uma empresa com este documento.");
        }

        var existingCompany = getCompanyById(request.id());
        Company company = companyMapper.toEntity(existingCompany, request);
        return companyMapper.toDTO(companyRepository.save(company));
    }

    public void delete(UUID id) {
        var existingCompany = getCompanyById(id);
        existingCompany.setActive(false);
        companyRepository.save(existingCompany);
    }

    public void activate(UUID id) {
        var existingCompany = findCompanyByIdAndDeleted(id);
        existingCompany.setActive(true);
        companyRepository.save(existingCompany);
    }

    private Company getCompanyById(UUID id) {
        Optional<Company> company = companyRepository.findCompanyByIdAndActiveTrue(id);

        if (company.isEmpty()) {
            throw new BusinessException("Empresa não encontrada.");
        }

        return company.get();
    }

    private Company findCompanyByIdAndDeleted(UUID id) {
        Optional<Company> company = companyRepository.findCompanyByIdAndActiveFalse(id);

        if (company.isEmpty()) {
            throw new BusinessException("Empresa não encontrada.");
        }

        return company.get();
    }
}
