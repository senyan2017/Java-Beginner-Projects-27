package ExpenseTracker;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Read-side calculations over recorded expenses.
 * <p>
 * Keeping the statistics behind an interface means new reports (for example a
 * monthly summary) can be added as additional methods or implementations
 * without touching the storage or the command-line layers.
 */
public interface ExpenseStatistics {

    /** Total spent in a single category ({@code 0.00} if the category is unknown). */
    BigDecimal totalForCategory(String category);

    /** Per-category totals, in the repository's category order. */
    Map<String, BigDecimal> totalsByCategory();

    /** Total spent across every category. */
    BigDecimal grandTotal();
}
