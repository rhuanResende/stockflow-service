package com.desenvolvimento.logica.stockflow.stockflow_service.user.repository.custom;

import com.desenvolvimento.logica.stockflow.stockflow_service.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserRepositoryCustom {
    Page<User> filter(
            UUID tenantId,
            UUID profileId,
            String name,
            String document,
            String status,
            Pageable pageable
    );
}
