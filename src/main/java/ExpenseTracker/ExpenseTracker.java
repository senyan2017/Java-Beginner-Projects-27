package ExpenseTracker;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Command-line front end for the expense tracker.
 * <p>
 * This class owns user interaction only. The data lives in an
 * {@link ExpenseRepository} and all calculations go through
 * {@link ExpenseStatistics}, so the menu logic stays small and the business
 * rules can be unit-tested without a console.
 */
public class ExpenseTracker {

    private final ExpenseRepository repository;
    private final ExpenseStatistics statistics;

    public ExpenseTracker(ExpenseRepository repository, ExpenseStatistics statistics) {
        this.repository = repository;
        this.statistics = statistics;
    }

    public static void main(String[] args) {
        ExpenseRepository repository = new ExpenseRepository();
        ExpenseStatistics statistics = new CategoryExpenseStatistics(repository);
        ExpenseTracker app = new ExpenseTracker(repository, statistics);

        try (Scanner scanner = new Scanner(System.in)) {
            app.run(scanner);
        }
    }

    /** Runs the menu loop until the user chooses to exit. */
    public void run(Scanner scanner) {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt(scanner, "Enter your choice: ");
            switch (choice) {
                case 1 -> recordExpense(scanner);
                case 2 -> printExpensesByCategory();
                case 3 -> printTotalsByCategory();
                case 4 -> {
                    System.out.println("Exiting Expense Tracker. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please select a valid option.");
            }
        }
    }

    private void printMenu() {
        System.out.println("Expense Tracker Menu:");
        System.out.println("1. Record an expense");
        System.out.println("2. View expenses by category");
        System.out.println("3. Calculate total expenses by category");
        System.out.println("4. Exit");
    }

    private void recordExpense(Scanner scanner) {
        BigDecimal amount = readAmount(scanner, "Enter the expense amount: ");
        System.out.print("Enter the category: ");
        String category = scanner.nextLine().trim();
        try {
            repository.record(new Expense(category, amount));
            System.out.println("Expense recorded.");
        } catch (IllegalArgumentException e) {
            System.out.println("Could not record expense: " + e.getMessage());
        }
    }

    private void printExpensesByCategory() {
        System.out.println("Expenses by Category:");
        if (repository.isEmpty()) {
            System.out.println("No expenses recorded yet.");
            return;
        }
        for (String category : repository.categories()) {
            System.out.println(category + ": " + formatAmounts(repository.amountsFor(category)));
        }
    }

    private void printTotalsByCategory() {
        System.out.println("Total Expenses by Category:");
        if (repository.isEmpty()) {
            System.out.println("No expenses recorded yet.");
            return;
        }
        for (Map.Entry<String, BigDecimal> entry : statistics.totalsByCategory().entrySet()) {
            System.out.println(entry.getKey() + ": " + Money.format(entry.getValue()));
        }
    }

    private String formatAmounts(List<BigDecimal> amounts) {
        StringBuilder line = new StringBuilder("[");
        for (int i = 0; i < amounts.size(); i++) {
            if (i > 0) {
                line.append(", ");
            }
            line.append(Money.format(amounts.get(i)));
        }
        return line.append("]").toString();
    }

    private int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private BigDecimal readAmount(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Money.parse(scanner.nextLine());
            } catch (IllegalArgumentException e) {
                System.out.println("Please enter a valid, non-negative amount (e.g., 12 or 12.50).");
            }
        }
    }
}
