package com.desenvolvimento.logica.stockflow.stockflow_service.user.repository;

import com.desenvolvimento.logica.stockflow.stockflow_service.user.entity.User;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.repository.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, UserRepositoryCustom {
    User findUserByDocumentAndActiveTrue(String document);
    User findUserByIdAndActiveTrue(UUID id);
    User findUserByIdAndActiveFalse(UUID id);
}
