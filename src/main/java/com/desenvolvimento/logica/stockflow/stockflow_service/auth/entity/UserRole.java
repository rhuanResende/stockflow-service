package com.desenvolvimento.logica.stockflow.stockflow_service.auth.entity;

import com.desenvolvimento.logica.stockflow.stockflow_common.entity.BaseEntity;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "tb_user_role", schema = "auth")
public class UserRole extends BaseEntity {

    @Column(name = "co_user", columnDefinition = "uuid")
    private UUID user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "co_role")
    private Role role;

}
