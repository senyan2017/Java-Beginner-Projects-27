package ExpenseTracker;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Stores and manages expense records, organized by category.
 */
public class ExpenseStore {
    private final Map<String, List<Expense>> data = new LinkedHashMap<>();

    /**
     * Records an expense into the store.
     */
    public void addExpense(Expense expense) {
        Objects.requireNonNull(expense, "Expense must not be null");
        data.computeIfAbsent(expense.getCategory(), k -> new ArrayList<>()).add(expense);
    }

    /**
     * Returns all categories that have at least one expense.
     */
    public Set<String> getCategories() {
        return Collections.unmodifiableSet(data.keySet());
    }

    /**
     * Returns expenses for a given category (empty list if category not found).
     */
    public List<Expense> getExpensesByCategory(String category) {
        return Collections.unmodifiableList(
                data.getOrDefault(category, Collections.emptyList()));
    }

    /**
     * Returns all expenses grouped by category, as an unmodifiable map.
     */
    public Map<String, List<Expense>> getAllExpenses() {
        return data.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        e -> Collections.unmodifiableList(e.getValue())));
    }

    /**
     * Returns total number of expense records across all categories.
     */
    public int size() {
        return data.values().stream().mapToInt(List::size).sum();
    }

    /**
     * Returns true if the store has no expenses.
     */
    public boolean isEmpty() {
        return data.isEmpty();
    }
}
