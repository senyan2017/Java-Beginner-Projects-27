package CurrencyConverter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Scanner;

/**
 * Simple command-line currency converter.
 *
 * <p>All exchange rates are expressed against a single base currency (Rupee).
 * Every conversion is derived from that single source of truth, which keeps the
 * rates internally consistent (A-&gt;B and B-&gt;A are reciprocals) and removes the
 * copy/paste errors that come with hand-writing each pairwise rate.
 */
public class CurrencyConverter {

    /** Choice number that exits the interactive loop. */
    public static final int EXIT_CHOICE = 0;

    /**
     * Supported currencies and their value expressed in the base currency (Rupee).
     *
     * <p>Example: {@code DOLLAR} has a rate of {@code 70.0}, meaning 1 Dollar = 70 Rupee.
     */
    public enum Currency {
        RUPEE("Rupee", 1.0),
        DOLLAR("Dollar", 70.0),
        POUND("Pound", 88.0),
        EURO("Euro", 80.0),
        YEN("Yen", 0.63),
        RINGGIT("Ringgit", 16.8);

        private final String displayName;
        private final double rateInBase;

        Currency(String displayName, double rateInBase) {
            this.displayName = displayName;
            this.rateInBase = rateInBase;
        }

        public String getDisplayName() {
            return displayName;
        }

        public double getRateInBase() {
            return rateInBase;
        }
    }

    // DecimalFormat is not thread-safe, but this CLI is single-threaded.
    // US symbols keep the output (and tests) deterministic regardless of the
    // default locale.
    private static final DecimalFormat MONEY_FORMAT =
            new DecimalFormat("#,##0.00", new DecimalFormatSymbols(Locale.US));

    /**
     * Converts {@code amount} of {@code from} into {@code to}.
     *
     * @throws IllegalArgumentException if a currency is null or the amount is not a
     *                                  positive, finite number.
     */
    public static double convert(Currency from, Currency to, double amount) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Currency must not be null");
        }
        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new IllegalArgumentException("Amount must be a finite number");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        return amount * (from.getRateInBase() / to.getRateInBase());
    }

    /** Formats a monetary value with a stable, two-decimal pattern. */
    public static String format(double value) {
        return MONEY_FORMAT.format(value);
    }

    /**
     * Parses a menu choice. Accepts the exit choice ({@value #EXIT_CHOICE}) and
     * any valid currency choice.
     *
     * @throws IllegalArgumentException if the text is not an integer in range.
     */
    public static int parseChoice(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Choice must not be empty");
        }
        int choice;
        try {
            choice = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Choice must be a whole number", e);
        }
        if (choice != EXIT_CHOICE && (choice < 1 || choice > Currency.values().length)) {
            throw new IllegalArgumentException(
                    "Choice must be between " + EXIT_CHOICE + " and " + Currency.values().length);
        }
        return choice;
    }

    /**
     * Parses a positive monetary amount.
     *
     * @throws IllegalArgumentException if the text is not a positive, finite number.
     */
    public static double parseAmount(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Amount must not be empty");
        }
        double amount;
        try {
            amount = Double.parseDouble(input.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Amount must be a number", e);
        }
        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new IllegalArgumentException("Amount must be a finite number");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        return amount;
    }

    /**
     * Maps a 1-based menu choice to a {@link Currency}.
     *
     * @throws IllegalArgumentException if the choice is not a valid currency number.
     */
    public static Currency currencyForChoice(int choice) {
        Currency[] values = Currency.values();
        if (choice < 1 || choice > values.length) {
            throw new IllegalArgumentException("No currency for choice " + choice);
        }
        return values[choice - 1];
    }

    private static void printMenu() {
        System.out.println("\nFollowing are the choices:");
        Currency[] values = Currency.values();
        for (int i = 0; i < values.length; i++) {
            System.out.println("Enter " + (i + 1) + ": " + values[i].getDisplayName());
        }
        System.out.println("Enter " + EXIT_CHOICE + ": Exit");
    }

    private static int readChoice(Scanner sc) {
        while (true) {
            System.out.print("\nChoose from above options: ");
            if (!sc.hasNextLine()) {
                return EXIT_CHOICE; // No more input (e.g. EOF) -> exit cleanly.
            }
            try {
                return parseChoice(sc.nextLine());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid choice: " + e.getMessage() + ". Please try again.");
            }
        }
    }

    private static double readAmount(Scanner sc) {
        while (true) {
            System.out.print("Enter the amount you want to convert: ");
            if (!sc.hasNextLine()) {
                throw new NoMoreInputException();
            }
            try {
                return parseAmount(sc.nextLine());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid amount: " + e.getMessage() + ". Please try again.");
            }
        }
    }

    private static void printConversions(Currency from, double amount) {
        for (Currency to : Currency.values()) {
            if (to == from) {
                continue;
            }
            double result = convert(from, to, amount);
            System.out.println(format(amount) + " " + from.getDisplayName()
                    + " = " + format(result) + " " + to.getDisplayName());
        }
    }

    /** Signals that the input stream ended while more input was expected. */
    private static final class NoMoreInputException extends RuntimeException {
    }

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                printMenu();
                int choice = readChoice(sc);
                if (choice == EXIT_CHOICE) {
                    System.out.println("Goodbye!");
                    break;
                }
                Currency from = currencyForChoice(choice);
                try {
                    double amount = readAmount(sc);
                    printConversions(from, amount);
                } catch (NoMoreInputException e) {
                    System.out.println("\nNo input received. Goodbye!");
                    break;
                }
            }
        }
    }
}
