package com.mottainai.crudinicial.security;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// CONSULTA JDBC DAS CREDENCIAIS DE ADMINISTRATION
// JdbcTemplate usa o DataSource gerenciado pelo Spring, compartilhado com JPA, e fecha os recursos da consulta.

// INTEGRAÇÃO JDBC: adapta AdministrationDAO.listByEmail sem ativar os CRUDs e deletes físicos legados.
// A igualdade parametrizada autentica uma conta exata; o JdbcTemplate reutiliza o DataSource do JPA.
@Repository
public class AdministrationCredentialsDao {
    private final JdbcTemplate jdbcTemplate;

    public AdministrationCredentialsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // CONSULTA PARAMETRIZADA E MAPEAMENTO
    // JdbcTemplate vincula email ao marcador ?, sem concatenar a entrada ao SQL, e mapeia as três colunas.
    // A fábrica antiga não é usada: duplicaria o DataSource; o DAO legado inclui escritas/exclusões fora do login.
    Optional<Credentials> findByEmail(String email) {
        return jdbcTemplate.query(
                "SELECT administrationid, email, passwordhash FROM administration WHERE email = ?",
                (rs, row) -> new Credentials(rs.getInt("administrationid"),
                        rs.getString("email"), rs.getString("passwordhash")), email)
                .stream().findFirst();
    }

    // DADOS RESTRITOS: o hash fica no pacote de autenticação e não é enviado ao JSP ou à sessão.
    record Credentials(int id, String email, String passwordHash) {
        @Override
        public String toString() {
            return "AdministrationCredentials[redacted]";
        }
    }
}
