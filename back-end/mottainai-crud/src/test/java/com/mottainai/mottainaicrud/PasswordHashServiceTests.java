package com.mottainai.mottainaicrud;

import com.mottainai.mottainaicrud.security.PasswordHashService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Protege a regra de 72 bytes do BCrypt, inclusive para caracteres UTF-8 e senhas com prefixo igual. */
class PasswordHashServiceTests {
    private final PasswordHashService passwords = new PasswordHashService();

    @Test void byteLimitIsCheckedBeforeHashing() {
        assertThrows(IllegalArgumentException.class, () -> passwords.hash("á".repeat(40)));
        String accepted="á".repeat(36);
        assertTrue(passwords.matches(accepted,passwords.hash(accepted)));
    }
    @Test void aLongerPasswordCannotMatchTheSameSeventyTwoBytePrefix() {
        String prefix="a".repeat(72),hash=passwords.hash(prefix);
        assertTrue(passwords.matches(prefix,hash));
        assertFalse(passwords.matches(prefix+"outro",hash));
    }
    @Test void missingOrMalformedCredentialsReturnFalse() {
        assertFalse(passwords.matches(null,"invalid"));
        assertFalse(passwords.matches("senha",null));
        assertFalse(passwords.matches("senha","hash inválido"));
    }
}
