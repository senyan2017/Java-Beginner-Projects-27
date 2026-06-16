package ExpenseTracker;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class UserInterfaceTest {

    private UserInterface createUI(String input, ByteArrayOutputStream outBuf) {
        Scanner scanner = new Scanner(new java.io.StringReader(input));
        PrintStream out = new PrintStream(outBuf);
        ExpenseStore store = new ExpenseStore();
        ExpenseStatistics stats = new ExpenseStatistics(store);
        return new UserInterface(scanner, out, store, stats);
    }

    @Test
    void exitOptionPrintsGoodbye() {
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        UserInterface ui = createUI("4\n", outBuf);
        ui.run();

        String output = outBuf.toString();
        assertTrue(output.contains("Exiting Expense Tracker. Goodbye!"));
    }

    @Test
    void invalidChoiceShowsErrorMessage() {
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        // First input "9" triggers invalid choice, then "4" exits
        UserInterface ui = createUI("9\n4\n", outBuf);
        ui.run();

        String output = outBuf.toString();
        assertTrue(output.contains("Invalid choice. Please select a valid option."));
    }

    @Test
    void viewExpensesWhenEmptyShowsNoExpensesMessage() {
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        // "2" views expenses (empty), then "4" exits
        UserInterface ui = createUI("2\n4\n", outBuf);
        ui.run();

        String output = outBuf.toString();
        assertTrue(output.contains("No expenses recorded yet."));
    }

    @Test
    void calculateTotalsWhenEmptyShowsNoExpensesMessage() {
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        // "3" calculates totals (empty), then "4" exits
        UserInterface ui = createUI("3\n4\n", outBuf);
        ui.run();

        String output = outBuf.toString();
        assertTrue(output.contains("No expenses recorded yet."));
    }

    @Test
    void recordAndThenViewExpenses() {
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        ExpenseStore store = new ExpenseStore();
        ExpenseStatistics stats = new ExpenseStatistics(store);
        String input = "1\n12.50\nFood\n2\n4\n";
        Scanner scanner = new Scanner(new java.io.StringReader(input));
        PrintStream out = new PrintStream(outBuf);

        UserInterface ui = new UserInterface(scanner, out, store, stats);
        ui.run();

        String output = outBuf.toString();
        assertTrue(output.contains("Expense recorded."));
        assertTrue(output.contains("Food"));
        assertTrue(output.contains("12.50"));
    }

    @Test
    void recordAndThenCalculateTotals() {
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        ExpenseStore store = new ExpenseStore();
        ExpenseStatistics stats = new ExpenseStatistics(store);
        String input = "1\n10\nFood\n1\n20\nFood\n3\n4\n";
        Scanner scanner = new Scanner(new java.io.StringReader(input));
        PrintStream out = new PrintStream(outBuf);

        UserInterface ui = new UserInterface(scanner, out, store, stats);
        ui.run();

        String output = outBuf.toString();
        assertTrue(output.contains("Total Expenses by Category:"));
        assertTrue(output.contains("Food: 30"));
    }
}
