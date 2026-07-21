package com.desenvolvimento.logica.stockflow.stockflow_service.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "tb_role", schema = "auth")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "co_id")
    private UUID id;

    @Column(name = "ds_name", nullable = false, length = 200)
    private String name;

}
