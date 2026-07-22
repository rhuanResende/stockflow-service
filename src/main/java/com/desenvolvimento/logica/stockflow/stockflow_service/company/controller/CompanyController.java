package com.desenvolvimento.logica.stockflow.stockflow_service.company.controller;

import com.desenvolvimento.logica.stockflow.stockflow_common.dto.ApiResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.dto.CompanyResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.enums.MessageCode;
import com.desenvolvimento.logica.stockflow.stockflow_common.service.MessageService;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.dto.CompanyRequest;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.dto.CompanyUpdateRequest;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${app.api.base}/company")
public class CompanyController {

    private final CompanyService companyService;
    private final MessageService messageService;

    public CompanyController(
            CompanyService companyService,
            MessageService messageService) {
        this.companyService = companyService;
        this.messageService = messageService;
    }

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('MASTER')")
    public ApiResponse<CompanyResponse> create(@Valid @RequestBody CompanyRequest request){
        return new ApiResponse<>(
                true,
                messageService.get(MessageCode.MESSAGE_SUCCESS_QUERY.getCode()),
                companyService.create(request)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MASTER')")
    public ApiResponse<CompanyResponse> getCompany(@PathVariable UUID id) {
        return new ApiResponse<>(
                true,
                messageService.get(MessageCode.MESSAGE_SUCCESS_QUERY.getCode()),
                companyService.findCompanyById(id)
        );
    }

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('MASTER')")
    public ApiResponse<Page<CompanyResponse>> getCompanies(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "ds_name") String sortBy,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "document", required = false) String document,
            @RequestParam(name = "status", defaultValue = "ATIVO", required = false) String status
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return new ApiResponse<>(
                true,
                messageService.get(MessageCode.MESSAGE_SUCCESS_QUERY.getCode()),
                companyService.getCompanies(name, document, status, pageable)
        );
    }

    @PutMapping()
    @PreAuthorize("hasAnyAuthority('MASTER')")
    public ApiResponse<CompanyResponse> update(@Valid @RequestBody CompanyUpdateRequest request) {
        return new ApiResponse<>(
                true,
                messageService.get(MessageCode.MESSAGE_SUCCESS_QUERY.getCode()),
                companyService.update(request)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MASTER')")
    public void delete(@PathVariable UUID id) {
        companyService.delete(id);
    }

    @PutMapping("/activate/{id}")
    @PreAuthorize("hasAnyAuthority('MASTER')")
    public void activate(@PathVariable UUID id) {
        companyService.activate(id);
    }
}
