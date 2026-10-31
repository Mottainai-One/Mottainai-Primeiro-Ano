package com.mottainai.crudinicial.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

// PROTEÇÃO IRREVERSÍVEL DE SENHAS — BCRYPT
// É compartilhado pelos fluxos atuais de Administration e UserAccount; somente hashes são persistidos.

@Service
public class PasswordHashService {
    private final BCryptPasswordEncoder passwordEncoder;
    public PasswordHashService() {

        // O BCrypt já adiciona um salt aleatório automaticamente.
        // Por isso duas senhas iguais não geram necessariamente
        // o mesmo hash no banco.
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    // GERAÇÃO DO HASH
    // O salt do BCrypt diferencia hashes da mesma senha; a aplicação persiste somente o resultado irreversível.
    public String hash(String password) {

        // A senha nunca deve ser armazenada diretamente.
        // Somente o hash irreversível é persistido.
        return passwordEncoder.encode(password);
    }
    // COMPARAÇÃO DA SENHA
    // matches usa o hash armazenado, incluindo seu salt, sem descriptografar senha e sem comparar dois hashes recém-gerados.
    public boolean matches(String password, String hash) {

        // Compara uma senha informada com o hash armazenado
        // sem precisar recuperar a senha original.
        return passwordEncoder.matches(password, hash);
    }
}