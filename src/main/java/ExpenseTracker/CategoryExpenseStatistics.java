package ExpenseTracker;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Computes category-based totals from an {@link ExpenseRepository}.
 * All sums use {@link BigDecimal} arithmetic so the results carry the same
 * precision guarantees as the stored amounts.
 */
public class CategoryExpenseStatistics implements ExpenseStatistics {

    private final ExpenseRepository repository;

    public CategoryExpenseStatistics(ExpenseRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository must not be null");
        }
        this.repository = repository;
    }

    @Override
    public BigDecimal totalForCategory(String category) {
        BigDecimal total = Money.ZERO;
        for (BigDecimal amount : repository.amountsFor(category)) {
            total = total.add(amount);
        }
        return Money.normalize(total);
    }

    @Override
    public Map<String, BigDecimal> totalsByCategory() {
        Map<String, BigDecimal> totals = new LinkedHashMap<>();
        for (String category : repository.categories()) {
            totals.put(category, totalForCategory(category));
        }
        return totals;
    }

    @Override
    public BigDecimal grandTotal() {
        BigDecimal total = Money.ZERO;
        for (String category : repository.categories()) {
            total = total.add(totalForCategory(category));
        }
        return Money.normalize(total);
    }
}
