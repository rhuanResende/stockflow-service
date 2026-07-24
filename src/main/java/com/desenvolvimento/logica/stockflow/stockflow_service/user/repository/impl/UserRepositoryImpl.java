package com.desenvolvimento.logica.stockflow.stockflow_service.user.repository.impl;

import com.desenvolvimento.logica.stockflow.stockflow_service.user.entity.User;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.repository.custom.UserRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final EntityManager em;

    public UserRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Page<User> filter(final UUID tenantId,
                             final UUID profileId,
                             final String name,
                             final String document,
                             final String status,
                             final Pageable pageable) {
        List<User> users = result(tenantId, profileId, name, document, status, pageable);
        long total = count(tenantId, profileId, name, document, status);
        return new PageImpl<>(users, pageable, total);
    }

    private List<User> result(final UUID tenantId,
                              final UUID profileId,
                              final String name,
                              final String document,
                              final String status,
                              final Pageable pageable) {
        String sql = select(false) +
                from(tenantId, profileId) +
                where(tenantId, profileId, name, document, status) +
                buildOrderBy(pageable);

        Query query = em.createNativeQuery(sql, User.class);
        setParameters(query, tenantId, profileId, name, document);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        return query.getResultList();
    }

    private long count(final UUID tenantId,
                       final UUID profileId,
                       final String name,
                       final String document,
                       final String status) {

        String sql = select(true) +
                from(tenantId, profileId) +
                where(tenantId, profileId, name, document, status);

        Query query = em.createNativeQuery(sql, Long.class);
        setParameters(query, tenantId, profileId, name, document);

        return ((Number) query.getSingleResult()).longValue();
    }

    private String select(boolean isCount) {
        if (isCount) {
            return "SELECT COUNT(*) \n";
        } else {
            return "SELECT u.* \n";
        }
    }

    private String from(final UUID tenantId,
                        final UUID profileId) {

        StringBuilder sql = new StringBuilder("FROM users.tb_user u \n");

        if (profileId != null) {
            sql.append("INNER JOIN auth.tb_user_role ur \n");
            sql.append("ON u.co_id = ur.co_user \n");
            sql.append("AND ur.st_active = true \n");
            sql.append("INNER JOIN auth.tb_role r \n");
            sql.append("ON ur.co_role = r.co_id \n");
            sql.append("AND r.st_active = true \n");
        }

        return sql.toString();
    }

    private String where(final UUID tenantId,
                         final UUID profileId,
                         final String name,
                         final String document,
                         final String status) {
        StringBuilder sql = new StringBuilder("WHERE 1=1 \n");

        if (tenantId != null) {
            sql.append("AND u.co_company = :tenantId \n");
        }

        if (profileId != null) {
            sql.append("AND r.co_id = :profileId \n");
        }

        if (name != null && !name.isBlank()) {
            sql.append("AND UNACCENT(UPPER(u.ds_name)) LIKE UNACCENT(UPPER(:name)) \n");
        }

        if (document != null && !document.isBlank()) {
            sql.append("AND UNACCENT(UPPER(u.nr_document)) LIKE UNACCENT(UPPER(:document)) \n");
        }

        if (status != null && !status.isBlank()) {
            if (status.equalsIgnoreCase("ATIVO")) {
                sql.append("AND u.st_active = true \n");
            } else {
                sql.append("AND u.st_active = false \n");
            }
        }

        return sql.toString();
    }

    private String buildOrderBy(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            return "ORDER BY u.co_id DESC";
        }

        StringBuilder orderBy = new StringBuilder(" ORDER BY ");

        pageable.getSort().forEach(order -> orderBy.append("u.")
                .append(order.getProperty())
                .append(" ")
                .append(order.getDirection())
                .append(", "));

        orderBy.delete(orderBy.length() - 2, orderBy.length());

        return orderBy.toString();
    }

    private void setParameters(Query query,
                               final UUID tenantId,
                               final UUID profileId,
                               final String name,
                               final String document) {
        if (tenantId != null) {
            query.setParameter("tenantId", tenantId);
        }

        if (profileId != null) {
            query.setParameter("profileId", profileId);
        }

        if (name != null && !name.isBlank()) {
            query.setParameter("name", "%" + name + "%");
        }

        if (document != null && !document.isBlank()) {
            query.setParameter("document", "%" + document + "%");
        }
    }
}
