package ExpenseTracker;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Central place for monetary rules. Money is always kept as a {@link BigDecimal}
 * with a fixed scale of 2 and HALF_UP rounding, so the rest of the application
 * never has to deal with floating-point rounding surprises.
 */
public final class Money {

    public static final int SCALE = 2;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    public static final BigDecimal ZERO = normalize(BigDecimal.ZERO);

    private Money() {
        // utility class
    }

    /** Returns {@code amount} rounded to the canonical money scale. */
    public static BigDecimal normalize(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
        return amount.setScale(SCALE, ROUNDING);
    }

    /** Parses user input into a normalized money value. */
    public static BigDecimal parse(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Amount must not be blank");
        }
        try {
            return normalize(new BigDecimal(raw.trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not a valid amount: " + raw.trim());
        }
    }

    /** Formats a money value for display, always with two decimals. */
    public static String format(BigDecimal amount) {
        return normalize(amount).toPlainString();
    }
}
