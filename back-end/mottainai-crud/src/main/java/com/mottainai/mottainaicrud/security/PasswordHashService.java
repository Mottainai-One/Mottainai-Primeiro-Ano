package com.mottainai.mottainaicrud.security;

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

        // BCrypt limita bytes, não caracteres: acentos podem ocupar mais de um byte em UTF-8.
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Senha obrigatória.");
        }
        if (password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Senha deve ter até 72 bytes em UTF-8.");
        }

        // A senha nunca deve ser armazenada diretamente.
        // Somente o hash irreversível é persistido.
        return passwordEncoder.encode(password);
    }
    // COMPARAÇÃO DA SENHA
    // matches usa o hash armazenado, incluindo seu salt, sem descriptografar senha e sem comparar dois hashes recém-gerados.
    public boolean matches(String password, String hash) {

        // Não permite autenticar uma senha maior usando apenas um prefixo de 72 bytes.
        if (password == null || hash == null ||
                password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            return false;
        }

        // Compara uma senha informada com o hash armazenado
        // sem precisar recuperar a senha original.
        try {
            return passwordEncoder.matches(password, hash);
        } catch (IllegalArgumentException invalidHash) {
            return false;
        }
    }
}
