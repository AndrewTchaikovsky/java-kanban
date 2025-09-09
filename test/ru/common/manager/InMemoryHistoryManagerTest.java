package ru.common.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.common.model.Epic;
import ru.common.model.Status;
import ru.common.model.Subtask;
import ru.common.model.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    private static TaskManager manager;
    private static HistoryManager historyManager;

    @BeforeAll
    static void setUp() {
        manager = Managers.getDefault();
        historyManager = manager.getHistoryManager();
    }

    @BeforeEach
    void beforeEachTest() {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        historyManager.clearHistory();
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
        int id1 = manager.createTask(task);
        manager.getTask(id1);
        array.add(task);

        Task task2 = new Task("Таск 2", "Описание таска 2", Status.NEW);
        int id2 = manager.createTask(task2);
        manager.getTask(id2);
        array.add(task2);

        Task task3 = new Task("Таск 3", "Описание таска 3", Status.NEW);
        int id3 = manager.createTask(task3);
        manager.getTask(id3);
        array.add(task3);

        List<Task> history = historyManager.getHistory();

        Assertions.assertTrue(history.containsAll(array) && array.containsAll(history), "Список задач отличается от списка, возвращаемого из истории задач.");
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

    @Test
    void changingTaskShouldChangeDataInBothManagers() {
        Task task = new Task("Таск 1", "Описание таска 1", Status.NEW);
        int id = manager.createTask(task);

        Task fromManager = manager.getTask(id);
        Task fromHistory = historyManager.getHistory().get(0);

        Assertions.assertEquals(task, fromManager, "Менеджер задач возвращает задачу отличную от оригинальной");
        Assertions.assertEquals(task, fromHistory, "Менеджер истории возвращает задачу отличную от оригинальной");

        task.setId(id + 100);
        task.setName("Новое имя");
        task.setDescription("Новое описание");
        task.setStatus(Status.DONE);

        Assertions.assertEquals(fromManager.getId(), fromHistory.getId(), "Айди задачи из менеджера задач и менеджера истории не совпадают.");
        Assertions.assertEquals(fromManager.getName(), fromHistory.getName(), "Имя задачи из менеджера задач и менеджера истории не совпадают.");
        Assertions.assertEquals(fromManager.getDescription(), fromHistory.getDescription(), "Описание задачи из менеджера задач и менеджера истории не совпадают.");
        Assertions.assertEquals(fromManager.getStatus(), fromHistory.getStatus(), "Статус задачи из менеджера задач и менеджера истории не совпадают.");

    }

    @Test
    void changingSubtaskShouldChangeDataInBothManagers() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic);

        Subtask subtask = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID);
        int subtaskID = manager.createSubtask(subtask);

        Subtask fromManager = manager.getSubtask(subtaskID);
        Task fromHistory = historyManager.getHistory().get(0);

        Assertions.assertEquals(subtask, fromManager, "Менеджер задач возвращает подзадачу отличную от оригинальной");
        Assertions.assertEquals(subtask, fromHistory, "Менеджер истории возвращает подзадачу отличную от оригинальной");

        subtask.setId(subtaskID + 100);
        subtask.setName("Новое имя");
        subtask.setDescription("Новое описание");
        subtask.setStatus(Status.DONE);

        Assertions.assertEquals(fromManager.getId(), fromHistory.getId(), "Айди подзадачи из менеджера задач и менеджера истории не совпадают.");
        Assertions.assertEquals(fromManager.getName(), fromHistory.getName(), "Имя подзадачи из менеджера задач и менеджера истории не совпадают.");
        Assertions.assertEquals(fromManager.getDescription(), fromHistory.getDescription(), "Описание подзадачи из менеджера задач и менеджера истории не совпадают.");
        Assertions.assertEquals(fromManager.getStatus(), fromHistory.getStatus(), "Статус подзадачи из менеджера задач и менеджера истории не совпадают.");

    }

    @Test
    void changingSubtaskShouldChangeEpicInBothManagers() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic);

        Subtask subtask = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID);
        int subtaskID = manager.createSubtask(subtask);

        manager.getSubtask(subtaskID);
        Epic epicFromManager = manager.getEpic(epicID);
        Epic epicFromHistory = (Epic) historyManager.getHistory().get(0);

        Assertions.assertEquals(epic, epicFromManager, "Менеджер задач возвращает эпик отличный от оригинального");
        Assertions.assertEquals(epic, epicFromHistory, "Менеджер истории возвращает эпик отличный от оригинального");

        subtask.setStatus(Status.DONE);

        Assertions.assertEquals(epicFromManager.getStatus(), epicFromHistory.getStatus(), "Статус эпика из менеджера задач и менеджера истории не совпадают.");

    }

    @Test
    void changingEpicShouldChangeDataInBothManagers() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int id = manager.createEpic(epic);

        Epic fromManager = manager.getEpic(id);
        Epic fromHistory = (Epic) historyManager.getHistory().get(0);

        Assertions.assertEquals(epic, fromManager, "Менеджер задач возвращает эпик отличный от оригинального");
        Assertions.assertEquals(epic, fromHistory, "Менеджер истории возвращает эпик отличный от оригинального");

        epic.setId(id + 100);
        epic.setName("Новое имя");
        epic.setDescription("Новое описание");
        epic.setStatus(Status.DONE);
        epic.setSubtaskIDs(List.of(1, 2, 3));

        Assertions.assertEquals(fromManager.getId(), fromHistory.getId(), "Айди эпика из менеджера задач и менеджера истории не совпадают.");
        Assertions.assertEquals(fromManager.getName(), fromHistory.getName(), "Имя эпика из менеджера задач и менеджера истории не совпадают.");
        Assertions.assertEquals(fromManager.getDescription(), fromHistory.getDescription(), "Описание эпика из менеджера задач и менеджера истории не совпадают.");
        Assertions.assertEquals(fromManager.getStatus(), fromHistory.getStatus(), "Статус эпика из менеджера задач и менеджера истории не совпадают.");
        Assertions.assertEquals(fromManager.getSubtaskIDs(), fromHistory.getSubtaskIDs(), "Список айди подзадач эпика из менеджера задач и менеджера истории не совпадают.");

    }

}