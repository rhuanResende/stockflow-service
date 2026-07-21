package com.desenvolvimento.logica.stockflow.stockflow_service.auth.repository;

import com.desenvolvimento.logica.stockflow.stockflow_service.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);
}
