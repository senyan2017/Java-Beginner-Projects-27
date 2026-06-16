package ExpenseTracker;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a single expense entry with a category and amount.
 */
public class Expense {
    private final String category;
    private final BigDecimal amount;

    public Expense(String category, BigDecimal amount) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category must not be empty");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.category = category.trim();
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Objects.equals(category, expense.category) &&
               amount.compareTo(expense.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, amount.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return amount.toPlainString();
    }
}
