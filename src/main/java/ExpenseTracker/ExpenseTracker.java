package ExpenseTracker;

import java.util.Scanner;

/**
 * Entry point for the Expense Tracker application.
 * <p>
 * Responsibilities are delegated to:
 * <ul>
 *   <li>{@link ExpenseStore} — data storage</li>
 *   <li>{@link ExpenseStatistics} — aggregation and reporting</li>
 *   <li>{@link UserInterface} — CLI input/output</li>
 * </ul>
 */
public class ExpenseTracker {
    public static void main(String[] args) {
        ExpenseStore store = new ExpenseStore();
        ExpenseStatistics statistics = new ExpenseStatistics(store);

        try (Scanner scanner = new Scanner(System.in)) {
            UserInterface ui = new UserInterface(scanner, System.out, store, statistics);
            ui.run();
        }
    }
}
