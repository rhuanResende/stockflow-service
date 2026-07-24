package com.desenvolvimento.logica.stockflow.stockflow_service.auth.service;

import com.desenvolvimento.logica.stockflow.stockflow_common.exception.BusinessException;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.dto.RoleResponse;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.entity.Role;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.mapper.RoleMapper;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public RoleResponse findRoleById(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Perfil não encontrado."));
        return roleMapper.toDTO(role);
    }
}
