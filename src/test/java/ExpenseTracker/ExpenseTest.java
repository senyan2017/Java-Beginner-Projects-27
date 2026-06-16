package ExpenseTracker;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseTest {

    @Test
    void createsExpenseWithValidInputs() {
        Expense expense = new Expense("Food", new BigDecimal("12.50"));
        assertEquals("Food", expense.getCategory());
        assertEquals(new BigDecimal("12.50"), expense.getAmount());
    }

    @Test
    void trimsCategoryWhitespace() {
        Expense expense = new Expense("  Transport  ", new BigDecimal("5"));
        assertEquals("Transport", expense.getCategory());
    }

    @Test
    void rejectsNullCategory() {
        assertThrows(IllegalArgumentException.class, () -> new Expense(null, new BigDecimal("10")));
    }

    @Test
    void rejectsBlankCategory() {
        assertThrows(IllegalArgumentException.class, () -> new Expense("   ", new BigDecimal("10")));
    }

    @Test
    void rejectsNullAmount() {
        assertThrows(IllegalArgumentException.class, () -> new Expense("Food", null));
    }

    @Test
    void rejectsZeroAmount() {
        assertThrows(IllegalArgumentException.class, () -> new Expense("Food", BigDecimal.ZERO));
    }

    @Test
    void rejectsNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> new Expense("Food", new BigDecimal("-5")));
    }

    @Test
    void toStringReturnsPlainAmount() {
        Expense expense = new Expense("Food", new BigDecimal("12.50"));
        assertEquals("12.50", expense.toString());
    }

    @Test
    void equalityComparesAmountCorrectly() {
        Expense a = new Expense("Food", new BigDecimal("10.0"));
        Expense b = new Expense("Food", new BigDecimal("10.00"));
        assertEquals(a, b);
    }

    @Test
    void notEqualWhenCategoriesDiffer() {
        Expense a = new Expense("Food", new BigDecimal("10"));
        Expense b = new Expense("Transport", new BigDecimal("10"));
        assertNotEquals(a, b);
    }
}
