package ExpenseTracker;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseRepositoryTest {

    @Test void newRepositoryIsEmpty() {
        assertTrue(new ExpenseRepository().isEmpty());
    }

    @Test void recordNormalizesAmountAndGroupsByCategory() {
        ExpenseRepository repository = new ExpenseRepository();
        repository.record(new Expense("Food", new BigDecimal("12.5")));
        repository.record(new Expense("Food", new BigDecimal("3.333")));

        assertFalse(repository.isEmpty());
        assertEquals(
                List.of(new BigDecimal("12.50"), new BigDecimal("3.33")),
                repository.amountsFor("Food"));
    }

    @Test void categoriesKeepFirstSeenOrder() {
        ExpenseRepository repository = new ExpenseRepository();
        repository.record(new Expense("Food", new BigDecimal("1")));
        repository.record(new Expense("Travel", new BigDecimal("2")));
        repository.record(new Expense("Food", new BigDecimal("3")));

        assertEquals(List.of("Food", "Travel"), List.copyOf(repository.categories()));
    }

    @Test void amountsForUnknownCategoryIsEmpty() {
        assertTrue(new ExpenseRepository().amountsFor("Nope").isEmpty());
    }

    @Test void recordRejectsNullExpense() {
        assertThrows(IllegalArgumentException.class, () -> new ExpenseRepository().record(null));
    }

    @Test void negativeAmountIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Expense("Food", new BigDecimal("-1")));
    }

    @Test void blankCategoryIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Expense("   ", new BigDecimal("1")));
    }

    @Test void storedAmountsCannotBeMutatedFromOutside() {
        ExpenseRepository repository = new ExpenseRepository();
        repository.record(new Expense("Food", new BigDecimal("1")));

        assertThrows(UnsupportedOperationException.class,
                () -> repository.amountsFor("Food").add(BigDecimal.TEN));
    }
}
