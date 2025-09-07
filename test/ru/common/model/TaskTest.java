package ru.common.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.common.manager.InMemoryTaskManager;
import ru.common.manager.Managers;
import ru.common.manager.TaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    private static TaskManager manager;

    @BeforeAll
    static void setUp() {
        manager = Managers.getDefault();
    }

    @Test
    void taskObjectsAreEqualIftheirIDsAreEqual() {
        Task task1 = new Task("Таск 1", "Описание таска 1", Status.NEW);
        int task1ID = manager.createTask(task1);
        Task task2 = new Task("Таск 2", "Описание таска 2", Status.IN_PROGRESS);
        InMemoryTaskManager.id = task1ID;
        manager.createTask(task2);
        System.out.println(task1);
        System.out.println(task2);
        assertEquals(task1, task2, "Объекты Task не равны друг другу если равен их ID");
    }


}