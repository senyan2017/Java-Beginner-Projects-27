package ExpenseTracker;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides statistical summaries over an {@link ExpenseStore}.
 * <p>
 * This service is designed to be extended with additional aggregation
 * capabilities (e.g. monthly summaries) without touching the storage layer.
 */
public class ExpenseStatistics {
    private final ExpenseStore store;

    public ExpenseStatistics(ExpenseStore store) {
        this.store = Objects.requireNonNull(store, "ExpenseStore must not be null");
    }

    /**
     * Returns the total amount spent in a given category.
     * Returns {@link BigDecimal#ZERO} if the category has no expenses.
     */
    public BigDecimal totalByCategory(String category) {
        return store.getExpensesByCategory(category).stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Returns a map of category -> total amount for all categories.
     * The returned map preserves insertion order of categories.
     */
    public Map<String, BigDecimal> totalsByAllCategories() {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (String category : store.getCategories()) {
            result.put(category, totalByCategory(category));
        }
        return Collections.unmodifiableMap(result);
    }

    /**
     * Returns the grand total across all categories.
     */
    public BigDecimal grandTotal() {
        return totalsByAllCategories().values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Returns the number of expense records in a given category.
     */
    public int countByCategory(String category) {
        return store.getExpensesByCategory(category).size();
    }

    /**
     * Returns the average expense amount in a given category.
     * Returns {@link BigDecimal#ZERO} if the category has no expenses.
     */
    public BigDecimal averageByCategory(String category) {
        List<Expense> expenses = store.getExpensesByCategory(category);
        if (expenses.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = totalByCategory(category);
        return total.divide(BigDecimal.valueOf(expenses.size()), 2, java.math.RoundingMode.HALF_UP);
    }
}
