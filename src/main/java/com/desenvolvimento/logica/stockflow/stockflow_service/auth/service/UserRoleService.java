package com.desenvolvimento.logica.stockflow.stockflow_service.auth.service;

import com.desenvolvimento.logica.stockflow.stockflow_common.exception.BusinessException;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.entity.UserRole;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.repository.UserRoleRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final RoleService roleService;

    public UserRoleService(
            UserRoleRepository userRoleRepository,
            RoleService roleService
    ) {
        this.userRoleRepository = userRoleRepository;
        this.roleService = roleService;
    }

    public void save(UUID user, UUID role) {
        UserRole current = userRoleRepository.findUserRoleByUserAndActiveTrue(user);
        if (current != null) {
            if (current.getRole().equals(role)) {
                return;
            }
            current.setActive(false);
            userRoleRepository.save(current);
            userRoleRepository.flush();
        }
        UserRole newUserRole = new UserRole();
        newUserRole.setUser(user);
        newUserRole.setRole(role);
        userRoleRepository.save(newUserRole);
    }

    public String findRoleByUser(UUID user) {
        UserRole userRole = userRoleRepository.findUserRoleByUserAndActiveTrue(user);
        if (userRole == null) {
            throw new BusinessException("Role não encontrada.");
        }
        return roleService.findRoleById(userRole.getRole()).name();
    }
}
