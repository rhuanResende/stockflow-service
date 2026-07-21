package com.desenvolvimento.logica.stockflow.stockflow_service.auth.entity;

import com.desenvolvimento.logica.stockflow.stockflow_service.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "tb_refresh_token", schema = "auth")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "co_id")
    private UUID id;

    @Column(name = "co_user", columnDefinition = "uuid")
    private UUID user;

    @Column(name = "ds_token", nullable = false, length = 500)
    private String token;

    @Column(name = "dt_expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "st_revoked", nullable = false)
    private boolean revoked;

    @Column(name = "dt_created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
    }

}
