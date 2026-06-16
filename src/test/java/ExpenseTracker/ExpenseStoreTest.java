package ExpenseTracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseStoreTest {

    private ExpenseStore store;

    @BeforeEach
    void setUp() {
        store = new ExpenseStore();
    }

    @Test
    void startsEmpty() {
        assertTrue(store.isEmpty());
        assertEquals(0, store.size());
    }

    @Test
    void addExpenseStoresSingleRecord() {
        store.addExpense(new Expense("Food", new BigDecimal("12.50")));
        assertFalse(store.isEmpty());
        assertEquals(1, store.size());
    }

    @Test
    void addExpenseGroupsByCategory() {
        store.addExpense(new Expense("Food", new BigDecimal("10")));
        store.addExpense(new Expense("Food", new BigDecimal("20")));
        store.addExpense(new Expense("Transport", new BigDecimal("5")));

        assertEquals(3, store.size());
        assertEquals(2, store.getExpensesByCategory("Food").size());
        assertEquals(1, store.getExpensesByCategory("Transport").size());
    }

    @Test
    void getCategoriesReturnsAllCategories() {
        store.addExpense(new Expense("Food", new BigDecimal("10")));
        store.addExpense(new Expense("Transport", new BigDecimal("5")));

        var categories = store.getCategories();
        assertEquals(2, categories.size());
        assertTrue(categories.contains("Food"));
        assertTrue(categories.contains("Transport"));
    }

    @Test
    void getExpensesByCategoryReturnsEmptyForUnknownCategory() {
        List<Expense> result = store.getExpensesByCategory("NonExistent");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllExpensesReturnsUnmodifiableView() {
        store.addExpense(new Expense("Food", new BigDecimal("10")));
        var all = store.getAllExpenses();

        assertThrows(UnsupportedOperationException.class, () ->
                all.put("Hacked", List.of()));
    }

    @Test
    void getExpensesByCategoryReturnsUnmodifiableList() {
        store.addExpense(new Expense("Food", new BigDecimal("10")));
        List<Expense> list = store.getExpensesByCategory("Food");

        assertThrows(UnsupportedOperationException.class, () ->
                list.add(new Expense("Hacked", new BigDecimal("1"))));
    }

    @Test
    void addExpenseRejectsNull() {
        assertThrows(NullPointerException.class, () -> store.addExpense(null));
    }
}
