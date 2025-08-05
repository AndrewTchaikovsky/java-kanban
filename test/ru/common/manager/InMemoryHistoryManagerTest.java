package ru.common.manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.common.model.Status;
import ru.common.model.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    private static TaskManager manager;
    private static HistoryManager historyManager;

    @BeforeAll
    static void setUp() {
        manager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void originalTaskIsKeptAfterAddingToHistoryManager() {
        Task task = new Task("Таск 1", "Описание таска 1", Status.NEW);
        manager.createTask(task);
        historyManager.add(task);
        Task taskStored = historyManager.getHistory().get(0);

        assertEquals(task.getId(), taskStored.getId(), "Айди задачи изменилось после добавления в историю.");
        assertEquals(task.getName(), taskStored.getName(), "Название задачи изменилось после добавления в историю.");
        assertEquals(task.getDescription(), taskStored.getDescription(), "Описание задачи изменилось после добавления в историю.");
        assertEquals(task.getStatus(), taskStored.getStatus(), "Статус задачи изменился после добавления в историю.");
    }
}