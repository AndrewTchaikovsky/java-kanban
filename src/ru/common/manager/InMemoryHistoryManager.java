package ru.common.manager;

import ru.common.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> lastTenTasks;

    public InMemoryHistoryManager() {
        this.lastTenTasks = new ArrayList<>(10);
    }

    @Override
    public void add(Task task) {
        if (lastTenTasks.size() == 10) {
            lastTenTasks.removeFirst();
        }
        lastTenTasks.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return lastTenTasks;
    }
}
