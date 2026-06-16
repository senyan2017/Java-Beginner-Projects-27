package ToDoListApp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    // ----- Priority.fromString -----

    @Test
    void priorityFromString_valid() {
        assertEquals(Task.Priority.HIGH, Task.Priority.fromString("HIGH"));
        assertEquals(Task.Priority.MEDIUM, Task.Priority.fromString("medium"));
        assertEquals(Task.Priority.LOW, Task.Priority.fromString("low"));
    }

    @Test
    void priorityFromString_invalid() {
        assertNull(Task.Priority.fromString("urgent"));
        assertNull(Task.Priority.fromString(""));
        assertNull(Task.Priority.fromString(null));
    }

    // ----- 序列化 / 反序列化 -----

    @Test
    void serializeAndDeserialize_fullTask() {
        Task task = new Task("买牛奶", Task.Priority.HIGH, LocalDate.of(2026, 6, 20));
        String line = task.serialize();
        Task restored = Task.deserialize(line);

        assertNotNull(restored);
        assertEquals("买牛奶", restored.getDescription());
        assertEquals(Task.Priority.HIGH, restored.getPriority());
        assertEquals(LocalDate.of(2026, 6, 20), restored.getDueDate());
        assertFalse(restored.isDone());
    }

    @Test
    void serializeAndDeserialize_doneTask() {
        Task task = new Task("写报告", Task.Priority.LOW, null, true);
        Task restored = Task.deserialize(task.serialize());

        assertNotNull(restored);
        assertTrue(restored.isDone());
        assertNull(restored.getDueDate());
    }

    @Test
    void deserialize_invalidLine() {
        assertNull(Task.deserialize(null));
        assertNull(Task.deserialize(""));
        assertNull(Task.deserialize("only_one_field"));
        assertNull(Task.deserialize("\tHIGH\t2026-01-01\tfalse")); // 空描述
    }

    @Test
    void deserialize_badDateStillWorks() {
        // 日期格式错误时 dueDate 应为 null，但任务本身仍可解析
        String line = "测试任务\tMEDIUM\t不是日期\tfalse";
        Task task = Task.deserialize(line);
        assertNotNull(task);
        assertNull(task.getDueDate());
    }

    @Test
    void deserialize_badPriorityDefaultsToMedium() {
        String line = "测试任务\tUNKNOWN\t2026-01-01\tfalse";
        Task task = Task.deserialize(line);
        assertNotNull(task);
        assertEquals(Task.Priority.MEDIUM, task.getPriority());
    }

    @Test
    void serialize_descriptionWithTabsAndNewlines() {
        Task task = new Task("desc\twith\ttabs\nand\nnewlines", Task.Priority.LOW, null);
        String line = task.serialize();
        // 不应包含 tab 或换行
        assertEquals(3, line.chars().filter(c -> c == '\t').count()); // 只有 3 个字段分隔符
        assertFalse(line.contains("\n"));
    }
}

class TaskStoreTest {

    @TempDir
    Path tempDir;

    private Path tempFile() {
        return tempDir.resolve("test_tasks.txt");
    }

    // ----- 基本 CRUD -----

    @Test
    void addAndGet() {
        TaskStore store = new TaskStore(tempFile());
        store.addTask(new Task("任务A", Task.Priority.HIGH, null));
        store.addTask(new Task("任务B", Task.Priority.LOW, null));

        assertEquals(2, store.size());
        assertEquals("任务A", store.getTask(0).getDescription());
        assertEquals("任务B", store.getTask(1).getDescription());
    }

    @Test
    void getTask_outOfRange() {
        TaskStore store = new TaskStore(tempFile());
        assertNull(store.getTask(0));
        assertNull(store.getTask(-1));
    }

    @Test
    void deleteTask() {
        TaskStore store = new TaskStore(tempFile());
        store.addTask(new Task("A", Task.Priority.HIGH, null));
        store.addTask(new Task("B", Task.Priority.LOW, null));

        assertTrue(store.deleteTask(0));
        assertEquals(1, store.size());
        assertEquals("B", store.getTask(0).getDescription());

        assertFalse(store.deleteTask(5)); // 越界
        assertFalse(store.deleteTask(-1));
    }

    // ----- 筛选 / 搜索 -----

    @Test
    void filterByStatus() {
        TaskStore store = new TaskStore(tempFile());
        Task t1 = new Task("A", Task.Priority.HIGH, null);
        Task t2 = new Task("B", Task.Priority.LOW, null, true);
        Task t3 = new Task("C", Task.Priority.MEDIUM, null);
        store.addTask(t1);
        store.addTask(t2);
        store.addTask(t3);

        assertEquals(3, store.filterByStatus(null).size());  // 全部
        assertEquals(2, store.filterByStatus(false).size()); // 未完成
        assertEquals(1, store.filterByStatus(true).size());  // 已完成
    }

    @Test
    void search() {
        TaskStore store = new TaskStore(tempFile());
        store.addTask(new Task("买菜", Task.Priority.HIGH, null));
        store.addTask(new Task("写代码", Task.Priority.LOW, null));
        store.addTask(new Task("买书", Task.Priority.MEDIUM, null));

        List<Task> results = store.search("买");
        assertEquals(2, results.size());

        results = store.search("代码");
        assertEquals(1, results.size());

        results = store.search("不存在");
        assertTrue(results.isEmpty());

        // 空关键词返回全部
        assertEquals(3, store.search("").size());
        assertEquals(3, store.search(null).size());
    }

    // ----- 持久化 -----

    @Test
    void saveAndLoad() throws IOException {
        TaskStore store1 = new TaskStore(tempFile());
        store1.addTask(new Task("任务甲", Task.Priority.HIGH, LocalDate.of(2026, 7, 1)));
        store1.addTask(new Task("任务乙", Task.Priority.LOW, null, true));
        store1.save();

        // 用新 store 加载同一文件
        TaskStore store2 = new TaskStore(tempFile());
        store2.load();

        assertEquals(2, store2.size());
        assertEquals("任务甲", store2.getTask(0).getDescription());
        assertEquals(Task.Priority.HIGH, store2.getTask(0).getPriority());
        assertEquals(LocalDate.of(2026, 7, 1), store2.getTask(0).getDueDate());
        assertFalse(store2.getTask(0).isDone());

        assertEquals("任务乙", store2.getTask(1).getDescription());
        assertTrue(store2.getTask(1).isDone());
        assertNull(store2.getTask(1).getDueDate());
    }

    @Test
    void load_nonExistentFile() throws IOException {
        TaskStore store = new TaskStore(tempDir.resolve("no_such_file.txt"));
        store.load(); // 不应抛出异常
        assertEquals(0, store.size());
    }

    @Test
    void load_skipsBlankAndInvalidLines() throws IOException {
        Path file = tempFile();
        Files.writeString(file, "\n\ninvalid_line\tonly_two\n" +
                "有效任务\tHIGH\t2026-08-01\tfalse\n");

        TaskStore store = new TaskStore(file);
        store.load();

        assertEquals(1, store.size());
        assertEquals("有效任务", store.getTask(0).getDescription());
    }

    @Test
    void save_overwritesPreviousContent() throws IOException {
        TaskStore store = new TaskStore(tempFile());
        store.addTask(new Task("旧任务", Task.Priority.LOW, null));
        store.save();

        // 清空后重新添加
        store.deleteTask(0);
        store.addTask(new Task("新任务", Task.Priority.HIGH, null));
        store.save();

        TaskStore store2 = new TaskStore(tempFile());
        store2.load();
        assertEquals(1, store2.size());
        assertEquals("新任务", store2.getTask(0).getDescription());
    }
}
