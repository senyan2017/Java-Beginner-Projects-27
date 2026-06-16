package ToDoListApp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 负责任务的增删查、状态筛选、关键词搜索，以及基于文本文件的持久化存储。
 */
public class TaskStore {

    private final Path filePath;
    private final List<Task> tasks;

    public TaskStore() {
        this(Paths.get("tasks.txt"));
    }

    public TaskStore(Path filePath) {
        this.filePath = filePath;
        this.tasks = new ArrayList<>();
    }

    // ----- 基本操作 -----

    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public int size() {
        return tasks.size();
    }

    public Task getTask(int index) {
        if (index < 0 || index >= tasks.size()) return null;
        return tasks.get(index);
    }

    public void addTask(Task task) {
        if (task != null) {
            tasks.add(task);
        }
    }

    public boolean deleteTask(int index) {
        if (index < 0 || index >= tasks.size()) return false;
        tasks.remove(index);
        return true;
    }

    // ----- 筛选 / 搜索 -----

    /**
     * 按状态筛选：
     *   null  -> 全部
     *   true  -> 已完成
     *   false -> 未完成
     */
    public List<Task> filterByStatus(Boolean done) {
        if (done == null) {
            return new ArrayList<>(tasks);
        }
        return tasks.stream()
                .filter(t -> t.isDone() == done)
                .collect(Collectors.toList());
    }

    /**
     * 按关键词搜索（不区分大小写，匹配任务描述）。
     */
    public List<Task> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return new ArrayList<>(tasks);
        }
        String lower = keyword.toLowerCase();
        return tasks.stream()
                .filter(t -> t.getDescription().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    // ----- 持久化 -----

    /**
     * 将所有任务保存到文件（每条任务一行，Tab 分隔）。
     */
    public void save() throws IOException {
        List<String> lines = tasks.stream()
                .map(Task::serialize)
                .collect(Collectors.toList());
        Files.write(filePath, lines);
    }

    /**
     * 从文件加载任务，文件不存在时静默跳过。
     */
    public void load() throws IOException {
        tasks.clear();
        if (!Files.exists(filePath)) return;
        List<String> lines = Files.readAllLines(filePath);
        for (String line : lines) {
            if (line.isBlank()) continue;
            Task task = Task.deserialize(line);
            if (task != null) {
                tasks.add(task);
            }
        }
    }

    /**
     * 返回当前使用的文件路径（便于测试或展示）。
     */
    public Path getFilePath() {
        return filePath;
    }
}
