package CurrencyConverter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyConverterTest {

    private static final double DELTA = 0.01;

    // --- Conversion correctness ---

    @Test
    void sameCurrencyConversionReturnsSameAmount() {
        double result = CurrencyConverter.convert(100.0, 0, 0);
        assertEquals(100.00, result, DELTA);
    }

    @Test
    void rupeeToDollar() {
        // 70 Rupee = 1 Dollar
        double result = CurrencyConverter.convert(70.0, 0, 1);
        assertEquals(1.00, result, DELTA);
    }

    @Test
    void dollarToRupee() {
        // 1 Dollar = 70 Rupee
        double result = CurrencyConverter.convert(1.0, 1, 0);
        assertEquals(70.00, result, DELTA);
    }

    @Test
    void dollarToEuro() {
        // 1 Dollar ≈ 0.87 Euro
        double result = CurrencyConverter.convert(1.0, 1, 3);
        assertEquals(0.87, result, DELTA);
    }

    @Test
    void euroToDollar() {
        double result = CurrencyConverter.convert(0.87, 3, 1);
        assertEquals(1.00, result, DELTA);
    }

    @Test
    void dollarToYen() {
        // 1 Dollar ≈ 111.087 Yen
        double result = CurrencyConverter.convert(1.0, 1, 4);
        assertEquals(111.09, result, DELTA);
    }

    @Test
    void dollarToRinggit() {
        // 1 Dollar ≈ 4.17 Ringgit
        double result = CurrencyConverter.convert(1.0, 1, 5);
        assertEquals(4.17, result, DELTA);
    }

    @Test
    void poundToDollar() {
        // 1 Pound ≈ 1.28 Dollar (1/0.78)
        double result = CurrencyConverter.convert(1.0, 2, 1);
        assertEquals(1.28, result, DELTA);
    }

    @Test
    void rupeeToPound() {
        // 70 Rupee = 1 Dollar, 1 Dollar ≈ 0.78 Pound → 70 Rupee ≈ 0.78 Pound
        double result = CurrencyConverter.convert(70.0, 0, 2);
        assertEquals(0.78, result, DELTA);
    }

    @Test
    void largeAmountConversion() {
        double result = CurrencyConverter.convert(1000000.0, 1, 0);
        assertEquals(70000000.00, result, 1.0);
    }

    @Test
    void smallAmountConversion() {
        double result = CurrencyConverter.convert(0.01, 1, 0);
        assertEquals(0.70, result, DELTA);
    }

    // --- Round-trip consistency ---

    @Test
    void roundTripDollarRupee() {
        double original = 100.0;
        double inRupees = CurrencyConverter.convert(original, 1, 0);
        double backToDollar = CurrencyConverter.convert(inRupees, 0, 1);
        assertEquals(original, backToDollar, 0.1);
    }

    @Test
    void roundTripEuroPound() {
        double original = 50.0;
        double inPounds = CurrencyConverter.convert(original, 3, 2);
        double backToEuro = CurrencyConverter.convert(inPounds, 2, 3);
        assertEquals(original, backToEuro, 0.1);
    }

    // --- Input validation ---

    @Test
    void zeroAmountThrows() {
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.convert(0, 1, 0));
    }

    @Test
    void negativeAmountThrows() {
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.convert(-10.0, 1, 0));
    }

    @Test
    void invalidFromIndexThrows() {
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.convert(10.0, -1, 0));
    }

    @Test
    void invalidToIndexThrows() {
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.convert(10.0, 0, 6));
    }

    @Test
    void invalidCurrencyNameThrows() {
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.getCurrencyName(-1));
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.getCurrencyName(6));
    }

    // --- isValidChoice ---

    @Test
    void validChoices() {
        for (int i = 1; i <= 6; i++) {
            assertTrue(CurrencyConverter.isValidChoice(i), "Choice " + i + " should be valid");
        }
    }

    @Test
    void invalidChoices() {
        assertFalse(CurrencyConverter.isValidChoice(0));
        assertFalse(CurrencyConverter.isValidChoice(7));
        assertFalse(CurrencyConverter.isValidChoice(-1));
    }

    // --- Currency names ---

    @Test
    void currencyNamesAreCapitalized() {
        String[] expected = {"Rupee", "Dollar", "Pound", "Euro", "Yen", "Ringgit"};
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], CurrencyConverter.getCurrencyName(i));
        }
    }

    // --- formatAmount ---

    @Test
    void formatAmountTwoDecimals() {
        assertEquals("1.00", CurrencyConverter.formatAmount(1.0));
        assertEquals("100.50", CurrencyConverter.formatAmount(100.5));
        assertEquals("0.10", CurrencyConverter.formatAmount(0.1));
    }

    @Test
    void formatAmountRounding() {
        assertEquals("1.24", CurrencyConverter.formatAmount(1.235));
        assertEquals("1.24", CurrencyConverter.formatAmount(1.244));
        assertEquals("1.25", CurrencyConverter.formatAmount(1.245));
    }

    // --- Cross-currency conversions (parameterized) ---

    @ParameterizedTest
    @CsvSource({
        "100.0, 0, 1, 1.43",   // Rupee -> Dollar
        "100.0, 0, 3, 1.25",   // Rupee -> Euro
        "100.0, 0, 4, 158.70", // Rupee -> Yen
        "100.0, 0, 5, 5.96",   // Rupee -> Ringgit
    })
    void rupeeConversions(double amount, int from, int to, double expected) {
        double result = CurrencyConverter.convert(amount, from, to);
        assertEquals(expected, result, 0.05);
    }

    @ParameterizedTest
    @CsvSource({
        "10.0, 1, 2, 7.80",    // Dollar -> Pound
        "10.0, 1, 3, 8.70",    // Dollar -> Euro
        "10.0, 1, 4, 1110.87", // Dollar -> Yen
        "10.0, 1, 5, 41.70",   // Dollar -> Ringgit
    })
    void dollarConversions(double amount, int from, int to, double expected) {
        double result = CurrencyConverter.convert(amount, from, to);
        assertEquals(expected, result, 0.05);
    }

    // --- All currencies can convert to all others ---

    @Test
    void allPairsProducePositiveResults() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                double result = CurrencyConverter.convert(100.0, i, j);
                assertTrue(result > 0,
                    "Converting " + CurrencyConverter.getCurrencyName(i)
                    + " to " + CurrencyConverter.getCurrencyName(j)
                    + " should produce a positive result, got: " + result);
            }
        }
    }
}
