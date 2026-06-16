package ToDoListApp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Holds the in-memory list of tasks and provides the operations the UI needs:
 * adding, editing, deleting, completing, filtering and searching.
 *
 * <p>All index parameters are zero-based. Methods that take an index return
 * {@code false} for out-of-range values instead of throwing, so callers can show
 * a friendly message.</p>
 */
public class TaskService {

    /** Which tasks a status filter should keep. */
    public enum StatusFilter {
        ALL,
        DONE,
        PENDING
    }

    private final List<Task> tasks;

    public TaskService() {
        this.tasks = new ArrayList<>();
    }

    public TaskService(List<Task> initialTasks) {
        this.tasks = new ArrayList<>(initialTasks == null ? List.of() : initialTasks);
    }

    public int size() {
        return tasks.size();
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    public boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /** Returns a read-only snapshot of all tasks in insertion order. */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(new ArrayList<>(tasks));
    }

    /** Returns the task at {@code index}, or {@code null} if out of range. */
    public Task get(int index) {
        return isValidIndex(index) ? tasks.get(index) : null;
    }

    public void add(Task task) {
        if (task != null) {
            tasks.add(task);
        }
    }

    /** Marks the task at {@code index} as done. */
    public boolean markDone(int index) {
        if (!isValidIndex(index)) {
            return false;
        }
        tasks.get(index).setDone(true);
        return true;
    }

    /** Replaces the description, priority and due date of the task at {@code index}. */
    public boolean edit(int index, String description, Priority priority, LocalDate dueDate) {
        if (!isValidIndex(index)) {
            return false;
        }
        Task task = tasks.get(index);
        task.setDescription(description);
        task.setPriority(priority);
        task.setDueDate(dueDate);
        return true;
    }

    /** Removes the task at {@code index}. */
    public boolean delete(int index) {
        if (!isValidIndex(index)) {
            return false;
        }
        tasks.remove(index);
        return true;
    }

    /** Returns tasks matching the given status filter. */
    public List<Task> filterByStatus(StatusFilter filter) {
        StatusFilter effective = filter == null ? StatusFilter.ALL : filter;
        List<Task> result = new ArrayList<>();
        for (Task task : tasks) {
            switch (effective) {
                case DONE:
                    if (task.isDone()) {
                        result.add(task);
                    }
                    break;
                case PENDING:
                    if (!task.isDone()) {
                        result.add(task);
                    }
                    break;
                case ALL:
                default:
                    result.add(task);
            }
        }
        return result;
    }

    /**
     * Returns tasks whose description contains {@code keyword}, ignoring case.
     * A blank or {@code null} keyword returns all tasks.
     */
    public List<Task> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(tasks);
        }
        String needle = keyword.trim().toLowerCase(Locale.ROOT);
        List<Task> result = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase(Locale.ROOT).contains(needle)) {
                result.add(task);
            }
        }
        return result;
    }
}
