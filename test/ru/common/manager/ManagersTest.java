package ru.common.manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.common.model.Status;
import ru.common.model.Task;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    private static TaskManager manager;
    private static HistoryManager historyManager;

    @BeforeAll
    static void setUp() {
        manager = Managers.getDefault();
        historyManager = manager.getHistoryManager();
    }

    @Test
    void shouldReturnInitializedTaskManager() {
        Task task1 = new Task("Таск 1", "Описание таска 1", Status.NEW);
        int taskID = manager.createTask(task1);
        Task returnedTask = manager.getTask(taskID);

        assertNotNull(manager, "Метод должен возвращать проинициализированный экземпляр менеджера.");
        assertNotNull(returnedTask, "Экземпляр задачи должен быть доступен после создания.");
        assertEquals(task1.getName(), returnedTask.getName());
    }

    @Test
    void shouldReturnInitializedHistoryManager() {
        Task task1 = new Task("Таск 1", "Описание таска 1", Status.NEW);
        manager.createTask(task1);
        historyManager.add(task1);

        assertNotNull(historyManager, "Метод должен возвращать проинициализированный экземпляр менеджера.");
        assertTrue(historyManager.getHistory().contains(task1), "Задача должна быть добавлена в историю просмотров.");

    }

}