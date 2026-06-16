package ExpenseTracker;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Handles all command-line interaction for the Expense Tracker.
 * <p>
 * This class is responsible for presenting menus, reading user input,
 * and formatting output. It delegates business logic to {@link ExpenseStore}
 * and {@link ExpenseStatistics}.
 */
public class UserInterface {
    private final Scanner scanner;
    private final PrintStream out;
    private final ExpenseStore store;
    private final ExpenseStatistics statistics;
    private boolean running = true;

    public UserInterface(Scanner scanner, PrintStream out, ExpenseStore store, ExpenseStatistics statistics) {
        this.scanner = scanner;
        this.out = out;
        this.store = store;
        this.statistics = statistics;
    }

    /**
     * Runs the main menu loop. Returns when the user chooses to exit.
     */
    public void run() {
        while (running) {
            showMenu();
            handleChoice(readChoice());
        }
    }

    private void showMenu() {
        out.println("Expense Tracker Menu:");
        out.println("1. Record an expense");
        out.println("2. View expenses by category");
        out.println("3. Calculate total expenses by category");
        out.println("4. Exit");
        out.print("Enter your choice: ");
    }

    private int readChoice() {
        try {
            String line = scanner.nextLine().trim();
            return Integer.parseInt(line);
        } catch (Exception e) {
            return -1; // signals invalid input
        }
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case 1 -> doRecordExpense();
            case 2 -> doViewByCategory();
            case 3 -> doCalculateTotals();
            case 4 -> {
                out.println("Exiting Expense Tracker. Goodbye!");
                running = false;
            }
            default -> out.println("Invalid choice. Please select a valid option.");
        }
    }

    private void doRecordExpense() {
        out.print("Enter the expense amount: ");
        BigDecimal amount = readAmount();
        if (amount == null) {
            out.println("Invalid amount. Please enter a positive number.");
            return;
        }

        out.print("Enter the category: ");
        String category = scanner.nextLine().trim();
        if (category.isEmpty()) {
            out.println("Category must not be empty.");
            return;
        }

        try {
            store.addExpense(new Expense(category, amount));
            out.println("Expense recorded.");
        } catch (IllegalArgumentException e) {
            out.println("Failed to record expense: " + e.getMessage());
        }
    }

    private BigDecimal readAmount() {
        String line = scanner.nextLine().trim();
        try {
            BigDecimal amount = new BigDecimal(line);
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                return amount;
            }
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void doViewByCategory() {
        if (store.isEmpty()) {
            out.println("No expenses recorded yet.");
            return;
        }
        out.println("Expenses by Category:");
        Map<String, List<Expense>> all = store.getAllExpenses();
        for (Map.Entry<String, List<Expense>> entry : all.entrySet()) {
            out.println(entry.getKey() + ": " + formatExpenseList(entry.getValue()));
        }
    }

    private void doCalculateTotals() {
        if (store.isEmpty()) {
            out.println("No expenses recorded yet.");
            return;
        }
        out.println("Total Expenses by Category:");
        Map<String, BigDecimal> totals = statistics.totalsByAllCategories();
        for (Map.Entry<String, BigDecimal> entry : totals.entrySet()) {
            out.println(entry.getKey() + ": " + entry.getValue().toPlainString());
        }
    }

    private String formatExpenseList(List<Expense> expenses) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < expenses.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(expenses.get(i).getAmount().toPlainString());
        }
        sb.append("]");
        return sb.toString();
    }
}
