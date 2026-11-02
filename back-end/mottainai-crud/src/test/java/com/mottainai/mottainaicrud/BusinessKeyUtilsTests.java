package com.mottainai.mottainaicrud;

import com.mottainai.mottainaicrud.util.BusinessKeyUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Verifica normalização sem aceitar letras nem alterar a identidade dos emails cadastrados. */
class BusinessKeyUtilsTests {
    @Test void cnpjAcceptsDigitsOrMaskAndRejectsIncompleteOrUnexpectedText() {
        assertEquals("22345678000199",BusinessKeyUtils.cnpj(" 22.345.678/0001-99 "));
        assertEquals("22345678000199",BusinessKeyUtils.cnpj("22345678000199"));
        for(String value:new String[]{"","123","abc22345678000199","22.345.678/0001-9x"}) {
            assertThrows(IllegalArgumentException.class,()->BusinessKeyUtils.cnpj(value));
        }
        assertThrows(IllegalArgumentException.class,()->BusinessKeyUtils.cnpj(null));
    }
    @Test void emailRemovesSurroundingSpacesButPreservesCase() {
        assertEquals("Pessoa@teste.example",BusinessKeyUtils.email(" Pessoa@teste.example "));
        assertThrows(IllegalArgumentException.class,()->BusinessKeyUtils.email(" "));
        assertThrows(IllegalArgumentException.class,()->BusinessKeyUtils.email(null));
    }
}
