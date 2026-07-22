package com.desenvolvimento.logica.stockflow.stockflow_service.company.repository.impl;

import com.desenvolvimento.logica.stockflow.stockflow_service.company.entity.Company;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.repository.custom.CompanyRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CompanyRepositoryImpl implements CompanyRepositoryCustom {

    private final EntityManager em;

    public CompanyRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Page<Company> filter(String name, String document, String status, Pageable pageable) {
        List<Company> companies = result(name, document, status, pageable);
        long total = count(name, document, status);
        return new PageImpl<>(companies, pageable, total);
    }

    @Override
    public List<Company> queryCompanyByFilterAndDeletedFalse(String filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * \n");
        sql.append("FROM company c \n");
        sql.append("WHERE 1=1 \n");
        sql.append("AND UNACCENT(UPPER(c.name)) LIKE UNACCENT(UPPER(:filter)) \n");
        sql.append("OR UNACCENT(UPPER(c.document)) LIKE UNACCENT(UPPER(:filter)) \n");
        sql.append("AND c.deleted = false \n");
        sql.append("ORDER BY c.name DESC");

        Query query = em.createNativeQuery(sql.toString(), Company.class);
        query.setParameter("filter", "%" + filter + "%");

        return query.getResultList();
    }

    private List<Company> result(String name, String document, String status, Pageable pageable) {
        String sql = select(false) +
                from() +
                where(name, document, status) +
                buildOrderBy(pageable);

        Query query = em.createNativeQuery(sql, Company.class);
        setParameters(query, name, document);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        return query.getResultList();
    }

    private long count(String name, String document, String status) {
        String sql = select(true) +
                from() +
                where(name, document, status);

        Query query = em.createNativeQuery(sql, Long.class);
        setParameters(query, name, document);

        return ((Number) query.getSingleResult()).longValue();
    }

    private String select(boolean isCount) {
        if (isCount) {
            return "SELECT COUNT(*) \n";
        } else {
            return "SELECT c.* \n";
        }
    }

    private String from() {
        return "FROM company.tb_company c \n";
    }

    private String where(String name, String document, String status) {
        StringBuilder sql = new StringBuilder("WHERE 1=1 \n");

        if (name != null && !name.isBlank()) {
            sql.append("AND UNACCENT(UPPER(c.ds_name)) LIKE UNACCENT(UPPER(:name)) \n");
        }

        if (document != null && !document.isBlank()) {
            sql.append("AND UNACCENT(UPPER(c.nr_document)) LIKE UNACCENT(UPPER(:document)) \n");
        }

        if (status != null && !status.isBlank()) {
            if (status.equalsIgnoreCase("ATIVO")) {
                sql.append("AND c.st_active = true \n");
            } else {
                sql.append("AND c.st_active = false \n");
            }
        }

        return sql.toString();
    }

    private String buildOrderBy(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            return "ORDER BY c.co_id DESC";
        }

        StringBuilder orderBy = new StringBuilder(" ORDER BY ");

        pageable.getSort().forEach(order -> orderBy.append("c.")
                .append(order.getProperty())
                .append(" ")
                .append(order.getDirection())
                .append(", "));

        orderBy.delete(orderBy.length() - 2, orderBy.length());

        return orderBy.toString();
    }

    private void setParameters(Query query, String name, String document) {
        if (name != null && !name.isBlank()) {
            query.setParameter("name", "%" + name + "%");
        }

        if (document != null && !document.isBlank()) {
            query.setParameter("document", "%" + document + "%");
        }
    }
}
