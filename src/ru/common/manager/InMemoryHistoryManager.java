package ru.common.manager;

import ru.common.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> latestTasks;
    public static final int NUMBER_OF_TASKS = 10;

    public InMemoryHistoryManager() {
        this.latestTasks = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (latestTasks.size() == NUMBER_OF_TASKS) {
            latestTasks.removeFirst();
        }
        latestTasks.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(latestTasks);
    }
}
