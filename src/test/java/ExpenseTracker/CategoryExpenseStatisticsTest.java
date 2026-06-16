package ExpenseTracker;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CategoryExpenseStatisticsTest {

    private ExpenseStatistics statisticsFor(ExpenseRepository repository) {
        return new CategoryExpenseStatistics(repository);
    }

    @Test void totalForCategorySumsAmounts() {
        ExpenseRepository repository = new ExpenseRepository();
        repository.record(new Expense("Food", new BigDecimal("12.50")));
        repository.record(new Expense("Food", new BigDecimal("7.25")));

        assertEquals(new BigDecimal("19.75"), statisticsFor(repository).totalForCategory("Food"));
    }

    @Test void totalForUnknownCategoryIsZero() {
        assertEquals(new BigDecimal("0.00"),
                statisticsFor(new ExpenseRepository()).totalForCategory("Nope"));
    }

    @Test void totalsByCategoryCoversEveryCategoryInOrder() {
        ExpenseRepository repository = new ExpenseRepository();
        repository.record(new Expense("Food", new BigDecimal("10")));
        repository.record(new Expense("Travel", new BigDecimal("5.5")));
        repository.record(new Expense("Travel", new BigDecimal("4.5")));

        Map<String, BigDecimal> totals = statisticsFor(repository).totalsByCategory();
        assertEquals(List.of("Food", "Travel"), List.copyOf(totals.keySet()));
        assertEquals(new BigDecimal("10.00"), totals.get("Food"));
        assertEquals(new BigDecimal("10.00"), totals.get("Travel"));
    }

    @Test void grandTotalSumsEveryCategory() {
        ExpenseRepository repository = new ExpenseRepository();
        repository.record(new Expense("Food", new BigDecimal("10")));
        repository.record(new Expense("Travel", new BigDecimal("5.5")));

        assertEquals(new BigDecimal("15.50"), statisticsFor(repository).grandTotal());
    }

    @Test void emptyRepositoryHasZeroGrandTotal() {
        assertEquals(new BigDecimal("0.00"), statisticsFor(new ExpenseRepository()).grandTotal());
    }

    @Test void constructorRejectsNullRepository() {
        assertThrows(IllegalArgumentException.class, () -> new CategoryExpenseStatistics(null));
    }
}
