package com.mottainai.crudinicial.dao;

import com.mottainai.crudinicial.model.*;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Query;

// ANÁLISE DAS QUERIES PELO HIBERNATE
// Carrega os mapeamentos e o dialeto PostgreSQL sem acesso JDBC; analisa JPQL, não executa as consultas no servidor.

class QueryValidationTest {
    @Test
    void hibernateParsesAllAddedQueriesWithActualEntityMappingsWithoutDatabase() {
        var registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.boot.allow_jdbc_metadata_access", false)
                .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .applySetting("hibernate.hbm2ddl.auto", "none")
                .build();
        try {
            var metadata = new MetadataSources(registry);
            for (Class<?> entity : new Class<?>[]{Address.class, Administration.class, Company.class,
                    Store.class, EmployeeRole.class, Employee.class, SubscriptionPlan.class, UserAccount.class}) {
                metadata.addAnnotatedClass(entity);
            }
            try (var factory = metadata.buildMetadata().buildSessionFactory(); var session = factory.openSession()) {
                for (Class<?> dao : new Class<?>[]{CompanyDao.class, StoreDao.class, EmployeeDao.class, UserAccountDao.class}) {
                    for (var method : dao.getDeclaredMethods()) {
                        var query = method.getAnnotation(Query.class);
                        if (query != null) session.createSelectionQuery(query.value());
                    }
                }
            }
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
