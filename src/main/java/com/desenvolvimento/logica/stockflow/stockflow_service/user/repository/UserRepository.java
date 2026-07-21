package com.desenvolvimento.logica.stockflow.stockflow_service.user.repository;

import com.desenvolvimento.logica.stockflow.stockflow_service.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findUserByDocument(String document);
    User findUserById(UUID id);
}
