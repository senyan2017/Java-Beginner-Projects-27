package ExpenseTracker;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test void normalizeRoundsHalfUpToTwoDecimals() {
        assertEquals(new BigDecimal("12.35"), Money.normalize(new BigDecimal("12.345")));
        assertEquals(new BigDecimal("12.34"), Money.normalize(new BigDecimal("12.344")));
    }

    @Test void parseAcceptsPlainNumbersAndTrimsWhitespace() {
        assertEquals(new BigDecimal("12.50"), Money.parse(" 12.5 "));
        assertEquals(new BigDecimal("12.00"), Money.parse("12"));
    }

    @Test void parseRejectsBlankNullAndGarbage() {
        assertThrows(IllegalArgumentException.class, () -> Money.parse(""));
        assertThrows(IllegalArgumentException.class, () -> Money.parse("   "));
        assertThrows(IllegalArgumentException.class, () -> Money.parse(null));
        assertThrows(IllegalArgumentException.class, () -> Money.parse("abc"));
    }

    @Test void formatAlwaysShowsTwoDecimals() {
        assertEquals("12.50", Money.format(new BigDecimal("12.5")));
        assertEquals("0.00", Money.format(BigDecimal.ZERO));
    }
}
