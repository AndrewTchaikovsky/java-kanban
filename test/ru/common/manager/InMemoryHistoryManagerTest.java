package ru.common.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.common.model.Status;
import ru.common.model.Task;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    void getHistoryReturnsTheSameArrayOfTasks() {
        List<Task> array = new ArrayList<>();

        Task task = new Task("Таск 1", "Описание таска 1", Status.NEW);
        manager.createTask(task);
        historyManager.add(task);
        array.add(task);

        Task task2 = new Task("Таск 2", "Описание таска 2", Status.NEW);
        manager.createTask(task2);
        historyManager.add(task2);
        array.add(task2);

        Task task3 = new Task("Таск 3", "Описание таска 3", Status.NEW);
        manager.createTask(task3);
        historyManager.add(task3);
        array.add(task3);

        Assertions.assertEquals(array, historyManager.getHistory(), "Список задач отличается от списка, возвращаемого из истории задач.");
    }

    @Test
    void shouldCorrectlyAddTasks() {
        Task task = new Task("Таск 1", "Описание таска 1", Status.NEW);
        manager.createTask(task);
        historyManager.add(task);

        Assertions.assertTrue(historyManager.getHistory().contains(task), "Зазача не была добавлена в историю.");
    }

    @Test
    void shouldCorrectlyDeleteTasks() {
        Task task = new Task("Таск 1", "Описание таска 1", Status.NEW);
        manager.createTask(task);
        historyManager.add(task);

        Assertions.assertTrue(historyManager.getHistory().contains(task), "Зазача не была добавлена в историю.");
        historyManager.remove(task.getId());
        Assertions.assertTrue(historyManager.getHistory().isEmpty(), "Задача не была удалена из истории.");
    }

}