package ExpenseTracker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * In-memory store of expenses grouped by category. Categories are kept in the
 * order they were first recorded so that listings are stable and predictable.
 * This class owns the "where is the data" concern only; how amounts are rounded
 * is enforced by {@link Expense}/{@link Money} before they ever land here.
 */
public class ExpenseRepository {

    private final Map<String, List<BigDecimal>> amountsByCategory = new LinkedHashMap<>();

    /** Records a validated expense under its category. */
    public void record(Expense expense) {
        if (expense == null) {
            throw new IllegalArgumentException("Expense must not be null");
        }
        amountsByCategory
                .computeIfAbsent(expense.category(), key -> new ArrayList<>())
                .add(expense.amount());
    }

    public boolean isEmpty() {
        return amountsByCategory.isEmpty();
    }

    /** Categories in first-seen order; the returned set is read-only. */
    public Set<String> categories() {
        return Collections.unmodifiableSet(amountsByCategory.keySet());
    }

    /**
     * Amounts recorded for the given category, in insertion order.
     * Returns an empty list for unknown categories. The returned list is read-only.
     */
    public List<BigDecimal> amountsFor(String category) {
        List<BigDecimal> amounts = amountsByCategory.get(category);
        return amounts == null ? List.of() : Collections.unmodifiableList(amounts);
    }
}
