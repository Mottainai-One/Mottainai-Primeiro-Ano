package com.mottainai.mottainaicrud.security;

import com.mottainai.mottainaicrud.dto.AdministrationResponseDTO;
import org.springframework.stereotype.Service;
import java.util.Optional;

// AUTENTICAÇÃO PELA TABELA ADMINISTRATION
// Consulta o DAO JDBC e usa BCrypt.matches; o resultado é um DTO restrito a ID/email, sem dados de UserAccount.


@Service
public class AdministrationAuthService {
    private final AdministrationCredentialsDao credentialsDao;
    private final PasswordHashService passwords;

    public AdministrationAuthService(AdministrationCredentialsDao credentialsDao, PasswordHashService passwords) {
        this.credentialsDao = credentialsDao;
        this.passwords = passwords;
    }

    // VALIDAÇÃO DO LOGIN
    // Email vazio, senha vazia, conta inexistente ou BCrypt inválido resultam em Optional vazio.
    // A comparação fica nesta camada para não entregar o hash à sessão ou à view.
    public Optional<AdministrationResponseDTO> authenticate(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return Optional.empty();
        }
        return credentialsDao.findByEmail(email.trim())
                .filter(account -> passwords.matches(password, account.passwordHash()))
                .map(account -> {
                    var response = new AdministrationResponseDTO();
                    response.setAdministrationId(account.id());
                    response.setEmail(account.email());
                    return response;
                });
    }
}
