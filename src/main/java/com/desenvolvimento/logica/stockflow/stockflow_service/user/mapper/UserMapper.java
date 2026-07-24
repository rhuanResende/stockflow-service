package com.desenvolvimento.logica.stockflow.stockflow_service.user.mapper;

import com.desenvolvimento.logica.stockflow.stockflow_common.dto.CompanyResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.dto.UserDataResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.dto.UserResponse;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.dto.UserCreateRequest;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.dto.UserUpdateRequest;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserMapper {

    public UserResponse toDTO(User user, CompanyResponse company, String role) {
        return new UserResponse(
                user.getId().toString(),
                user.getName(),
                user.getDocument(),
                user.getEmail(),
                user.getPhone(),
                user.getPassword(),
                user.getFirstAccess(),
                company,
                user.getActive() ? "ATIVO" : "INATIVO",
                role.equals("ADMIN") ?
                        "ADMINISTRADOR" :
                        role.equals("MANAGER") ?
                                "GERENTE" :
                                role.equals("USER") ?
                                        "USUÁRIO" :
                                        role
        );
    }

    public UserDataResponse toUserDataDTO(User user, CompanyResponse company, String role) {
        return new UserDataResponse(
                user.getId().toString(),
                user.getName().toUpperCase(),
                user.getDocument(),
                user.getEmail(),
                user.getPhone(),
                company,
                role.equals("ADMIN") ?
                        "ADMINISTRADOR" :
                        role.equals("MANAGER") ?
                                "GERENTE" :
                                role.equals("USER") ?
                                        "USUÁRIO" :
                                        role,
                user.getActive() ? "ATIVO" : "INATIVO"
        );
    }

    public User toEntity(UserCreateRequest request, CompanyResponse company, String temporaryPassword) {
        User user = new User();
        user.setCompany(UUID.fromString(company.id()));
        user.setName(request.name().toUpperCase());
        user.setDocument(request.document());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setPassword(temporaryPassword);
        user.setFirstAccess(true);
        user.setActive(true);
        user.setFailedLoginAttempts(0);
        return user;
    }

    public User toEntity(User user, CompanyResponse company, UserUpdateRequest request) {
        user.setCompany(UUID.fromString(company.id()));
        user.setName(request.name().toUpperCase());
        user.setDocument(request.document());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        return user;
    }
}
