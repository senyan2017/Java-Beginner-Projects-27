package CurrencyConverter;

import org.junit.jupiter.api.Test;

import static CurrencyConverter.CurrencyConverter.Currency;
import static org.junit.jupiter.api.Assertions.*;

class CurrencyConverterTest {

    private static final double EPS = 1e-6;

    // ---------- convert: correctness ----------

    @Test void sameCurrencyIsIdentity() {
        assertEquals(123.45, CurrencyConverter.convert(Currency.RUPEE, Currency.RUPEE, 123.45), EPS);
    }

    @Test void rupeeToDollar() {
        // 1 Dollar = 70 Rupee  ->  70 Rupee = 1 Dollar
        assertEquals(1.0, CurrencyConverter.convert(Currency.RUPEE, Currency.DOLLAR, 70.0), EPS);
    }

    @Test void dollarToRupee() {
        assertEquals(70.0, CurrencyConverter.convert(Currency.DOLLAR, Currency.RUPEE, 1.0), EPS);
    }

    @Test void dollarToYen() {
        assertEquals(70.0 / 0.63, CurrencyConverter.convert(Currency.DOLLAR, Currency.YEN, 1.0), EPS);
    }

    @Test void ringgitRateIsConsistent() {
        // Regression guard: the original code mixed 16 and 16.8 for Ringgit.
        assertEquals(16.8, CurrencyConverter.convert(Currency.RINGGIT, Currency.RUPEE, 1.0), EPS);
    }

    // ---------- convert: rates are internally consistent ----------

    @Test void roundTripReturnsOriginal() {
        for (Currency from : Currency.values()) {
            for (Currency to : Currency.values()) {
                double there = CurrencyConverter.convert(from, to, 100.0);
                double back = CurrencyConverter.convert(to, from, there);
                assertEquals(100.0, back, 1e-4,
                        "round trip " + from + " <-> " + to + " should be lossless");
            }
        }
    }

    // ---------- convert: invalid input ----------

    @Test void convertRejectsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> CurrencyConverter.convert(Currency.DOLLAR, Currency.EURO, 0.0));
    }

    @Test void convertRejectsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> CurrencyConverter.convert(Currency.DOLLAR, Currency.EURO, -5.0));
    }

    @Test void convertRejectsNaN() {
        assertThrows(IllegalArgumentException.class,
                () -> CurrencyConverter.convert(Currency.DOLLAR, Currency.EURO, Double.NaN));
    }

    @Test void convertRejectsInfinity() {
        assertThrows(IllegalArgumentException.class,
                () -> CurrencyConverter.convert(Currency.DOLLAR, Currency.EURO, Double.POSITIVE_INFINITY));
    }

    @Test void convertRejectsNullCurrency() {
        assertThrows(IllegalArgumentException.class,
                () -> CurrencyConverter.convert(null, Currency.EURO, 1.0));
    }

    // ---------- parseChoice ----------

    @Test void parseChoiceAcceptsValidNumbers() {
        assertEquals(0, CurrencyConverter.parseChoice("0"));
        assertEquals(1, CurrencyConverter.parseChoice("1"));
        assertEquals(6, CurrencyConverter.parseChoice("6"));
    }

    @Test void parseChoiceTrimsWhitespace() {
        assertEquals(2, CurrencyConverter.parseChoice("  2  "));
    }

    @Test void parseChoiceRejectsOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.parseChoice("7"));
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.parseChoice("-1"));
    }

    @Test void parseChoiceRejectsNonNumeric() {
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.parseChoice("abc"));
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.parseChoice(""));
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.parseChoice("   "));
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.parseChoice(null));
    }

    // ---------- parseAmount ----------

    @Test void parseAmountAcceptsPositiveNumbers() {
        assertEquals(100.0, CurrencyConverter.parseAmount("100"), EPS);
        assertEquals(12.5, CurrencyConverter.parseAmount("  12.5  "), EPS);
    }

    @Test void parseAmountRejectsZeroAndNegative() {
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.parseAmount("0"));
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.parseAmount("-5"));
    }

    @Test void parseAmountRejectsNonNumericAndNonFinite() {
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.parseAmount("abc"));
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.parseAmount(""));
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.parseAmount(null));
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.parseAmount("NaN"));
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.parseAmount("Infinity"));
    }

    // ---------- currencyForChoice ----------

    @Test void currencyForChoiceMapsCorrectly() {
        assertEquals(Currency.RUPEE, CurrencyConverter.currencyForChoice(1));
        assertEquals(Currency.RINGGIT, CurrencyConverter.currencyForChoice(6));
    }

    @Test void currencyForChoiceRejectsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.currencyForChoice(0));
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.currencyForChoice(7));
    }

    // ---------- format ----------

    @Test void formatIsStableTwoDecimals() {
        assertEquals("1,234.50", CurrencyConverter.format(1234.5));
        assertEquals("0.50", CurrencyConverter.format(0.5));
        assertEquals("2.00", CurrencyConverter.format(2.0));
    }
}
