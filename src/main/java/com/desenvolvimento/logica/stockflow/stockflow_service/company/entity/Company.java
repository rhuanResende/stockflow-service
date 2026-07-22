package com.desenvolvimento.logica.stockflow.stockflow_service.company.entity;

import com.desenvolvimento.logica.stockflow.stockflow_common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "tb_company", schema = "company")
public class Company extends BaseEntity {

    @Column(name = "ds_name", nullable = false, length = 200)
    private String name;

    @Column(name = "nr_document", nullable = false, length = 14)
    private String document;

}
