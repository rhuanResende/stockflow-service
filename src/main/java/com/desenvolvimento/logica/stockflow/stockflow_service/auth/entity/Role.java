package com.desenvolvimento.logica.stockflow.stockflow_service.auth.entity;

import com.desenvolvimento.logica.stockflow.stockflow_common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "tb_role", schema = "auth")
public class Role extends BaseEntity {

    @Column(name = "ds_name", nullable = false, length = 200)
    private String name;

    @Column(name = "ds_description", length = 255)
    private String description;

}
