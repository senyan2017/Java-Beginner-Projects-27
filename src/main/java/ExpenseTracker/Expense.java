package ExpenseTracker;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A single recorded expense: an {@code amount} charged against a {@code category}.
 * Instances are immutable and validated on construction, so an {@code Expense}
 * is always in a consistent, ready-to-store state.
 */
public final class Expense {

    private final String category;
    private final BigDecimal amount;

    public Expense(String category, BigDecimal amount) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category must not be blank");
        }
        BigDecimal normalized = Money.normalize(amount);
        if (normalized.signum() < 0) {
            throw new IllegalArgumentException("Amount must not be negative");
        }
        this.category = category.trim();
        this.amount = normalized;
    }

    public String category() {
        return category;
    }

    public BigDecimal amount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Expense other)) {
            return false;
        }
        return category.equals(other.category) && amount.equals(other.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, amount);
    }

    @Override
    public String toString() {
        return category + ": " + Money.format(amount);
    }
}
