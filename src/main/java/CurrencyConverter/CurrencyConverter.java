package CurrencyConverter;

import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyConverter {

    // Exchange rates relative to USD
    private static final Map<String, Double> RATES_TO_USD = new LinkedHashMap<>();

    static {
        RATES_TO_USD.put("Rupee", 1.0 / 70.0);
        RATES_TO_USD.put("Dollar", 1.0);
        RATES_TO_USD.put("Pound", 1.0 / 0.78);
        RATES_TO_USD.put("Euro", 1.0 / 0.87);
        RATES_TO_USD.put("Yen", 1.0 / 111.087);
        RATES_TO_USD.put("Ringgit", 1.0 / 4.17);
    }

    private static final String[] CURRENCY_NAMES = {
        "Rupee", "Dollar", "Pound", "Euro", "Yen", "Ringgit"
    };

    /**
     * Convert an amount from one currency to another.
     *
     * @param amount the amount to convert (must be > 0)
     * @param fromIndex source currency index (0-5)
     * @param toIndex target currency index (0-5)
     * @return converted amount rounded to 2 decimal places
     */
    public static double convert(double amount, int fromIndex, int toIndex) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (fromIndex < 0 || fromIndex >= CURRENCY_NAMES.length) {
            throw new IllegalArgumentException("Invalid source currency index: " + fromIndex);
        }
        if (toIndex < 0 || toIndex >= CURRENCY_NAMES.length) {
            throw new IllegalArgumentException("Invalid target currency index: " + toIndex);
        }

        String fromCurrency = CURRENCY_NAMES[fromIndex];
        String toCurrency = CURRENCY_NAMES[toIndex];

        // Convert to USD first, then to target currency
        double amountInUsd = amount * RATES_TO_USD.get(fromCurrency);
        double result = amountInUsd / RATES_TO_USD.get(toCurrency);

        return BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Get the display name for a currency by index.
     */
    public static String getCurrencyName(int index) {
        if (index < 0 || index >= CURRENCY_NAMES.length) {
            throw new IllegalArgumentException("Invalid currency index: " + index);
        }
        return CURRENCY_NAMES[index];
    }

    /**
     * Check if a choice is valid (1-6).
     */
    public static boolean isValidChoice(int choice) {
        return choice >= 1 && choice <= CURRENCY_NAMES.length;
    }

    /**
     * Format an amount to 2 decimal places.
     */
    public static String formatAmount(double amount) {
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            System.out.println("=== Currency Converter ===");
            System.out.println("Following are the choices:");
            for (int i = 0; i < CURRENCY_NAMES.length; i++) {
                System.out.println("Enter " + (i + 1) + ": " + CURRENCY_NAMES[i]);
            }

            // Read and validate choice
            int choice = readChoice(sc);
            if (choice == -1) {
                return; // user chose to exit
            }

            // Read and validate amount
            double amount = readAmount(sc);
            if (amount == -1) {
                return; // user chose to exit
            }

            // Perform conversions
            String fromCurrency = getCurrencyName(choice - 1);
            System.out.println("\n--- Conversion Results ---");
            System.out.println("Converting " + formatAmount(amount) + " " + fromCurrency + " to:");
            System.out.println();

            for (int i = 0; i < CURRENCY_NAMES.length; i++) {
                if (i == choice - 1) {
                    continue; // skip self-conversion
                }
                double converted = convert(amount, choice - 1, i);
                System.out.println("  " + formatAmount(amount) + " " + fromCurrency
                        + " = " + formatAmount(converted) + " " + CURRENCY_NAMES[i]);
            }

        } finally {
            sc.close();
        }
    }

    private static int readChoice(Scanner sc) {
        while (true) {
            System.out.print("\nChoose from above options (1-" + CURRENCY_NAMES.length + "), or 0 to exit: ");
            String input = sc.nextLine().trim();

            try {
                int choice = Integer.parseInt(input);
                if (choice == 0) {
                    System.out.println("Exiting. Goodbye!");
                    return -1;
                }
                if (isValidChoice(choice)) {
                    return choice;
                }
                System.out.println("Invalid choice. Please enter a number between 1 and " + CURRENCY_NAMES.length + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private static double readAmount(Scanner sc) {
        while (true) {
            System.out.print("Enter the amount you want to convert (or 0 to exit): ");
            String input = sc.nextLine().trim();

            try {
                double amount = Double.parseDouble(input);
                if (amount == 0) {
                    System.out.println("Exiting. Goodbye!");
                    return -1;
                }
                if (amount < 0) {
                    System.out.println("Amount cannot be negative. Please enter a positive number.");
                    continue;
                }
                return amount;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}
