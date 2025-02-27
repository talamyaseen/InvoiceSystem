package io.invoice_system.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;

@Repository
public class GeneralRepository {

    @Autowired
    private EntityManager entityManager;

    public List<Object> executeSql(String sql) {
        return  entityManager.createNativeQuery(sql).getResultList();
    }
}
