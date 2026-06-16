package ToDoListApp;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final TaskStore store = new TaskStore();

    public static void main(String[] args) {
        // 启动时加载历史任务
        try {
            store.load();
            System.out.println("已从 " + store.getFilePath() + " 加载 " + store.size() + " 条任务。");
        } catch (IOException e) {
            System.out.println("警告：无法读取历史文件（" + e.getMessage() + "），将从空列表开始。");
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMenu();
            String input = readLine(scanner, "").trim();
            int choice = parseInt(input, -1);

            switch (choice) {
                case 1 -> doAddTask(scanner);
                case 2 -> doListTasks(scanner);
                case 3 -> doMarkDone(scanner);
                case 4 -> doEditTask(scanner);
                case 5 -> doDeleteTask(scanner);
                case 6 -> doSearch(scanner);
                case 0 -> {
                    running = false;
                    doSave();
                    System.out.println("已保存并退出，再见！");
                }
                default -> System.out.println("无效选项，请重新输入。");
            }
        }
        scanner.close();
    }

    // ----- 菜单 -----

    private static void printMenu() {
        System.out.println();
        System.out.println("====== 待办事项应用 ======");
        System.out.println("  1. 新增任务");
        System.out.println("  2. 查看任务列表");
        System.out.println("  3. 标记完成");
        System.out.println("  4. 编辑任务");
        System.out.println("  5. 删除任务");
        System.out.println("  6. 搜索任务");
        System.out.println("  0. 保存并退出");
        System.out.print("请输入选项编号: ");
    }

    // ----- 功能实现 -----

    /** 新增任务 */
    private static void doAddTask(Scanner scanner) {
        System.out.print("任务描述: ");
        String desc = readLine(scanner, "").trim();
        if (desc.isEmpty()) {
            System.out.println("描述不能为空，已取消。");
            return;
        }

        Task.Priority priority = readPriority(scanner, "优先级 (HIGH/MEDIUM/LOW，直接回车默认 MEDIUM): ");
        LocalDate dueDate = readDate(scanner, "截止日期 (yyyy-MM-dd，直接回车跳过): ");

        Task task = new Task(desc, priority, dueDate);
        store.addTask(task);
        autoSave();
        System.out.println("任务已添加。");
    }

    /** 查看任务列表（支持按状态筛选） */
    private static void doListTasks(Scanner scanner) {
        System.out.println("筛选方式: [a]全部  [d]未完成  [x]已完成  (直接回车默认全部)");
        String filter = readLine(scanner, "a").trim().toLowerCase();

        Boolean done = null;
        if (filter.equals("d")) done = false;
        else if (filter.equals("x")) done = true;

        List<Task> list = store.filterByStatus(done);
        printTaskList(list);
    }

    /** 标记完成 / 取消完成 */
    private static void doMarkDone(Scanner scanner) {
        List<Task> all = store.getTasks();
        if (all.isEmpty()) {
            System.out.println("当前没有任何任务。");
            return;
        }
        printTaskList(all);
        int idx = readTaskIndex(scanner, all.size());
        if (idx < 0) return;

        Task t = all.get(idx);
        t.setDone(!t.isDone());
        autoSave();
        System.out.println("任务已" + (t.isDone() ? "标记为完成" : "恢复为未完成") + "。");
    }

    /** 编辑任务 */
    private static void doEditTask(Scanner scanner) {
        List<Task> all = store.getTasks();
        if (all.isEmpty()) {
            System.out.println("当前没有任何任务。");
            return;
        }
        printTaskList(all);
        int idx = readTaskIndex(scanner, all.size());
        if (idx < 0) return;

        Task t = all.get(idx);
        System.out.println("当前任务: " + t);
        System.out.println("（直接回车保留原值）");

        System.out.print("新描述 [" + t.getDescription() + "]: ");
        String newDesc = readLine(scanner, "").trim();
        if (!newDesc.isEmpty()) t.setDescription(newDesc);

        Task.Priority newPri = readPriorityOptional(scanner, "新优先级 (HIGH/MEDIUM/LOW，回车保留) [" + t.getPriority() + "]: ");
        if (newPri != null) t.setPriority(newPri);

        LocalDate newDate = readDateOptional(scanner, "新截止日期 (yyyy-MM-dd，回车保留，输入 none 清除): ");
        if (newDate != null) t.setDueDate(newDate);

        autoSave();
        System.out.println("任务已更新: " + t);
    }

    /** 删除任务 */
    private static void doDeleteTask(Scanner scanner) {
        List<Task> all = store.getTasks();
        if (all.isEmpty()) {
            System.out.println("当前没有任何任务。");
            return;
        }
        printTaskList(all);
        int idx = readTaskIndex(scanner, all.size());
        if (idx < 0) return;

        Task removed = all.get(idx);
        System.out.print("确认删除任务 \"" + removed.getDescription() + "\" ? (y/n): ");
        String confirm = readLine(scanner, "n").trim().toLowerCase();
        if (confirm.equals("y") || confirm.equals("yes")) {
            store.deleteTask(idx);
            autoSave();
            System.out.println("任务已删除。");
        } else {
            System.out.println("已取消删除。");
        }
    }

    /** 搜索任务 */
    private static void doSearch(Scanner scanner) {
        System.out.print("请输入搜索关键词: ");
        String keyword = readLine(scanner, "").trim();
        if (keyword.isEmpty()) {
            System.out.println("关键词不能为空。");
            return;
        }
        List<Task> results = store.search(keyword);
        if (results.isEmpty()) {
            System.out.println("未找到包含 \"" + keyword + "\" 的任务。");
        } else {
            System.out.println("搜索结果（共 " + results.size() + " 条）:");
            printTaskList(results);
        }
    }

    // ----- 辅助方法 -----

    private static void printTaskList(List<Task> list) {
        if (list.isEmpty()) {
            System.out.println("（暂无任务）");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            System.out.println((i + 1) + ". " + list.get(i));
        }
    }

    private static String readLine(Scanner scanner, String defaultVal) {
        try {
            String line = scanner.nextLine();
            return line.isEmpty() ? defaultVal : line;
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private static int parseInt(String s, int defaultVal) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /**
     * 读取优先级，用户直接回车则返回 MEDIUM。
     */
    private static Task.Priority readPriority(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = readLine(scanner, "").trim();
            if (input.isEmpty()) return Task.Priority.MEDIUM;
            Task.Priority p = Task.Priority.fromString(input);
            if (p != null) return p;
            System.out.println("无效的优先级，请输入 HIGH、MEDIUM 或 LOW。");
        }
    }

    /**
     * 读取优先级（可选），用户直接回车则返回 null（表示不修改）。
     */
    private static Task.Priority readPriorityOptional(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = readLine(scanner, "").trim();
            if (input.isEmpty()) return null;
            Task.Priority p = Task.Priority.fromString(input);
            if (p != null) return p;
            System.out.println("无效的优先级，请输入 HIGH、MEDIUM 或 LOW。");
        }
    }

    /**
     * 读取日期，用户直接回车则返回 null。
     */
    private static LocalDate readDate(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = readLine(scanner, "").trim();
            if (input.isEmpty()) return null;
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("日期格式错误，请使用 yyyy-MM-dd 格式（如 2026-06-20）。");
            }
        }
    }

    /**
     * 读取日期（可选），返回 null 表示不修改；输入 "none" 表示清除日期。
     */
    private static LocalDate readDateOptional(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = readLine(scanner, "").trim();
            if (input.isEmpty()) return null;          // 不修改
            if (input.equalsIgnoreCase("none")) return LocalDate.of(1, 1, 1); // 哨兵值，表示清除
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("日期格式错误，请使用 yyyy-MM-dd 格式（如 2026-06-20）。");
            }
        }
    }

    /**
     * 读取任务编号（1-based），返回 0-based 索引；无效时返回 -1。
     */
    private static int readTaskIndex(Scanner scanner, int maxSize) {
        System.out.print("请输入任务编号 (1-" + maxSize + "): ");
        String input = readLine(scanner, "").trim();
        int num = parseInt(input, -1);
        if (num < 1 || num > maxSize) {
            System.out.println("编号超出范围，已取消。");
            return -1;
        }
        return num - 1;
    }

    private static void autoSave() {
        try {
            store.save();
        } catch (IOException e) {
            System.out.println("警告：自动保存失败（" + e.getMessage() + "），退出时将再次尝试保存。");
        }
    }

    private static void doSave() {
        try {
            store.save();
        } catch (IOException e) {
            System.out.println("保存失败：" + e.getMessage());
        }
    }
}
