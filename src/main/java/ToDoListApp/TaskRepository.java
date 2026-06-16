package ToDoListApp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes tasks to a plain-text file so they survive between runs.
 *
 * <p>Each task is stored on one line as {@code done|priority|dueDate|description}.
 * The description is escaped so that pipes and newlines inside it never break the
 * format.</p>
 */
public class TaskRepository {

    private static final String FIELD_SEPARATOR = "|";

    /**
     * Loads tasks from {@code path}. Returns an empty list if the file does not
     * exist yet. Lines that cannot be parsed are skipped rather than aborting
     * the whole load.
     */
    public List<Task> load(Path path) throws IOException {
        List<Task> tasks = new ArrayList<>();
        if (path == null || !Files.exists(path)) {
            return tasks;
        }
        for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            if (line.trim().isEmpty()) {
                continue;
            }
            Task task = fromLine(line);
            if (task != null) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    /**
     * Writes all tasks to {@code path}, creating parent directories if needed.
     * Overwrites any existing content.
     */
    public void save(Path path, List<Task> tasks) throws IOException {
        if (path == null) {
            throw new IOException("No storage path provided.");
        }
        Path parent = path.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        List<String> lines = new ArrayList<>(tasks.size());
        for (Task task : tasks) {
            lines.add(toLine(task));
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    /** Serializes a task to a single storage line. */
    static String toLine(Task task) {
        String due = task.getDueDate() == null ? "" : task.getDueDate().toString();
        return (task.isDone() ? "1" : "0")
                + FIELD_SEPARATOR + task.getPriority().name()
                + FIELD_SEPARATOR + due
                + FIELD_SEPARATOR + escape(task.getDescription());
    }

    /**
     * Parses a storage line back into a task. Returns {@code null} when the line
     * is too damaged to recover.
     */
    static Task fromLine(String line) {
        List<String> parts = splitEscaped(line);
        if (parts.size() < 4) {
            return null;
        }
        boolean done = "1".equals(parts.get(0).trim());
        Priority priority = Priority.fromStringOrDefault(parts.get(1), Priority.DEFAULT);
        LocalDate dueDate = parseDate(parts.get(2));
        // Re-join any trailing fields so a stray separator in old data is tolerated.
        String rawDescription = String.join(FIELD_SEPARATOR, parts.subList(3, parts.size()));
        String description = unescape(rawDescription);
        return new Task(description, done, priority, dueDate);
    }

    private static LocalDate parseDate(String text) {
        String trimmed = text == null ? "" : text.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(trimmed);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private static String escape(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\':
                    sb.append("\\\\");
                    break;
                case '|':
                    sb.append("\\|");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String unescape(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(++i);
                switch (next) {
                    case '\\':
                        sb.append('\\');
                        break;
                    case '|':
                        sb.append('|');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    default:
                        sb.append(next);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /** Splits on unescaped pipe characters, keeping escape pairs intact. */
    private static List<String> splitEscaped(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\\' && i + 1 < line.length()) {
                current.append(c).append(line.charAt(++i));
            } else if (c == '|') {
                parts.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        parts.add(current.toString());
        return parts;
    }
}
