package ToDoListApp;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Task {

    public enum Priority {
        LOW, MEDIUM, HIGH;

        /**
         * 从字符串解析优先级，大小写不敏感，非法值返回 null。
         */
        public static Priority fromString(String s) {
            if (s == null || s.isBlank()) return null;
            try {
                return valueOf(s.toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    private String description;
    private Priority priority;
    private LocalDate dueDate;
    private boolean isDone;

    public Task(String description, Priority priority, LocalDate dueDate) {
        this(description, priority, dueDate, false);
    }

    public Task(String description, Priority priority, LocalDate dueDate, boolean isDone) {
        this.description = description;
        this.priority = priority != null ? priority : Priority.MEDIUM;
        this.dueDate = dueDate;
        this.isDone = isDone;
    }

    // ----- getters / setters -----

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    // ----- 序列化 / 反序列化 -----

    /**
     * 将任务序列化为 Tab 分隔的单行文本。
     * 格式：description\tpriority\tdueDate\tisDone
     */
    public String serialize() {
        String safeDesc = description
                .replace("\t", " ")
                .replace("\n", " ")
                .replace("\r", " ");
        String dateStr = (dueDate != null) ? dueDate.toString() : "";
        return safeDesc + "\t" + priority.name() + "\t" + dateStr + "\t" + isDone;
    }

    /**
     * 从一行 Tab 分隔的文本反序列化出 Task。
     * 字段缺失或格式错误时返回 null。
     */
    public static Task deserialize(String line) {
        if (line == null || line.isBlank()) return null;
        String[] parts = line.split("\t", -1);
        if (parts.length < 4) return null;

        String description = parts[0].trim();
        if (description.isEmpty()) return null;

        Priority priority = Priority.fromString(parts[1]);
        if (priority == null) priority = Priority.MEDIUM;

        LocalDate dueDate = null;
        if (!parts[2].isBlank()) {
            try {
                dueDate = LocalDate.parse(parts[2].trim());
            } catch (DateTimeParseException e) {
                // 日期无法解析则留空
            }
        }

        boolean isDone = Boolean.parseBoolean(parts[3].trim());
        return new Task(description, priority, dueDate, isDone);
    }

    @Override
    public String toString() {
        String status = isDone ? "[x]" : "[ ]";
        String dateStr = (dueDate != null) ? dueDate.toString() : "无截止日";
        return status + " [" + priority + "] " + description + " (截止: " + dateStr + ")";
    }
}
