package com.desenvolvimento.logica.stockflow.stockflow_service.auth.mapper;

import com.desenvolvimento.logica.stockflow.stockflow_service.auth.dto.RoleResponse;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public RoleResponse toDTO(Role role) {
        return new RoleResponse(
                role.getId().toString(),
                role.getName().toUpperCase(),
                role.getDescription(),
                role.getActive() ? "ATIVO" : "INATIVO"
        );
    }
}
