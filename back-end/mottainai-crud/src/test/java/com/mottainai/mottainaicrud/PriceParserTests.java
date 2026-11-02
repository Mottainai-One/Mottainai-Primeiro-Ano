package com.mottainai.mottainaicrud;

import com.mottainai.mottainaicrud.util.PriceParser;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Verifica os formatos monetários aceitos e a rejeição de entradas ambíguas. */
class PriceParserTests {
    @Test void acceptsCommaDotAndBrazilianThousands() {
        assertEquals(new BigDecimal("209.89"), PriceParser.parse("209,89"));
        assertEquals(new BigDecimal("209.89"), PriceParser.parse("209.89"));
        assertEquals(new BigDecimal("1209.89"), PriceParser.parse("1.209,89"));
        assertEquals(new BigDecimal("209"), PriceParser.parse("209"));
        assertNull(PriceParser.parse(" "));
    }
    @Test void rejectsAmbiguousOrOverPreciseValues() {
        for (String text : new String[]{"1,2,3", "209,899", "abc", "1.2.3", "1,209.89"}) {
            assertThrows(IllegalArgumentException.class, () -> PriceParser.parse(text), text);
        }
    }
}
