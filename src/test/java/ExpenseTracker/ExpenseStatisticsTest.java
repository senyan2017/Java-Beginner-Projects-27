package ExpenseTracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseStatisticsTest {

    private ExpenseStore store;
    private ExpenseStatistics stats;

    @BeforeEach
    void setUp() {
        store = new ExpenseStore();
        stats = new ExpenseStatistics(store);
    }

    @Test
    void totalByCategoryReturnsZeroForEmptyCategory() {
        assertEquals(BigDecimal.ZERO, stats.totalByCategory("Food"));
    }

    @Test
    void totalByCategorySumsCorrectly() {
        store.addExpense(new Expense("Food", new BigDecimal("10.50")));
        store.addExpense(new Expense("Food", new BigDecimal("20.30")));

        assertEquals(new BigDecimal("30.80"), stats.totalByCategory("Food"));
    }

    @Test
    void totalsByAllCategoriesReturnsCorrectMap() {
        store.addExpense(new Expense("Food", new BigDecimal("10")));
        store.addExpense(new Expense("Food", new BigDecimal("20")));
        store.addExpense(new Expense("Transport", new BigDecimal("5.50")));

        var totals = stats.totalsByAllCategories();
        assertEquals(2, totals.size());
        assertEquals(new BigDecimal("30"), totals.get("Food"));
        assertEquals(new BigDecimal("5.50"), totals.get("Transport"));
    }

    @Test
    void grandTotalSumsAllCategories() {
        store.addExpense(new Expense("Food", new BigDecimal("10")));
        store.addExpense(new Expense("Transport", new BigDecimal("5")));
        store.addExpense(new Expense("Entertainment", new BigDecimal("15.75")));

        assertEquals(new BigDecimal("30.75"), stats.grandTotal());
    }

    @Test
    void grandTotalReturnsZeroWhenEmpty() {
        assertEquals(BigDecimal.ZERO, stats.grandTotal());
    }

    @Test
    void countByCategoryReturnsCorrectCount() {
        store.addExpense(new Expense("Food", new BigDecimal("10")));
        store.addExpense(new Expense("Food", new BigDecimal("20")));

        assertEquals(2, stats.countByCategory("Food"));
        assertEquals(0, stats.countByCategory("NonExistent"));
    }

    @Test
    void averageByCategoryReturnsZeroForEmptyCategory() {
        assertEquals(BigDecimal.ZERO, stats.averageByCategory("Food"));
    }

    @Test
    void averageByCategoryComputesCorrectly() {
        store.addExpense(new Expense("Food", new BigDecimal("10")));
        store.addExpense(new Expense("Food", new BigDecimal("20")));

        assertEquals(new BigDecimal("15.00"), stats.averageByCategory("Food"));
    }

    @Test
    void averageByCategoryRoundsHalfUp() {
        store.addExpense(new Expense("Food", new BigDecimal("10")));
        store.addExpense(new Expense("Food", new BigDecimal("11")));
        store.addExpense(new Expense("Food", new BigDecimal("12")));

        // (10 + 11 + 12) / 3 = 11.00
        assertEquals(new BigDecimal("11.00"), stats.averageByCategory("Food"));
    }

    @Test
    void totalsByAllCategoriesIsUnmodifiable() {
        store.addExpense(new Expense("Food", new BigDecimal("10")));
        var totals = stats.totalsByAllCategories();

        assertThrows(UnsupportedOperationException.class, () ->
                totals.put("Hacked", BigDecimal.ONE));
    }

    @Test
    void precisionIsPreservedWithBigDecimal() {
        // Classic double precision issue: 0.1 + 0.2 != 0.3
        store.addExpense(new Expense("Food", new BigDecimal("0.10")));
        store.addExpense(new Expense("Food", new BigDecimal("0.20")));

        assertEquals(new BigDecimal("0.30"), stats.totalByCategory("Food"));
    }

    @Test
    void constructorRejectsNullStore() {
        assertThrows(NullPointerException.class, () -> new ExpenseStatistics(null));
    }
}
