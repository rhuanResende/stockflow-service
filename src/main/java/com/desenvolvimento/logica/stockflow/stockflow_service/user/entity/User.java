package com.desenvolvimento.logica.stockflow.stockflow_service.user.entity;

import com.desenvolvimento.logica.stockflow.stockflow_common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "tb_users", schema = "users")
public class User extends BaseEntity {

    @Column(name = "co_tenant", nullable = false)
    private UUID tenant;

    @Column(name = "ds_name", nullable = false, length = 200)
    private String name;

    @Column(name = "ds_document", nullable = false, unique = true, length = 11)
    private String document;

    @Column(name = "ds_email", nullable = false, length = 200)
    private String email;

    @Column(name = "ds_phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "ds_password", nullable = false)
    private String password;

    @Column(name = "st_first_access", nullable = false)
    private Boolean firstAccess;

    @Column(name = "dt_password_changed_at")
    private LocalDateTime passwordChangedAt;

    @Column(name = "dt_password_expires_at")
    private LocalDateTime passwordExpiresAt;

}
