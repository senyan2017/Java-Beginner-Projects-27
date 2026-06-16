package ToDoListApp;

import java.time.LocalDate;
import java.util.Objects;

/**
 * A single to-do item: what to do, whether it is done, how urgent it is, and
 * when it is due.
 */
public class Task {
    private String description;
    private boolean done;
    private Priority priority;
    private LocalDate dueDate; // may be null when no deadline is set

    public Task(String description) {
        this(description, false, Priority.DEFAULT, null);
    }

    public Task(String description, Priority priority, LocalDate dueDate) {
        this(description, false, priority, dueDate);
    }

    public Task(String description, boolean done, Priority priority, LocalDate dueDate) {
        this.description = normalizeDescription(description);
        this.done = done;
        this.priority = priority == null ? Priority.DEFAULT : priority;
        this.dueDate = dueDate;
    }

    private static String normalizeDescription(String description) {
        return description == null ? "" : description.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = normalizeDescription(description);
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority == null ? Priority.DEFAULT : priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        Task task = (Task) o;
        return done == task.done
                && Objects.equals(description, task.description)
                && priority == task.priority
                && Objects.equals(dueDate, task.dueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, done, priority, dueDate);
    }

    @Override
    public String toString() {
        return "Task{" +
                "description='" + description + '\'' +
                ", done=" + done +
                ", priority=" + priority +
                ", dueDate=" + dueDate +
                '}';
    }
}
