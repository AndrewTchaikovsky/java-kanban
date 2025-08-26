package ru.common.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.common.model.Epic;
import ru.common.model.Status;
import ru.common.model.Subtask;
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
        historyManager = manager.getHistoryManager();
    }

    @BeforeEach
    void beforeEachTest() {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        historyManager.getHistory().clear();
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
    void changingTaskCopyShouldNotAffectHistoryManager() {
        Task task = new Task("Таск 1", "Описание таска 1", Status.NEW);
        int id = manager.createTask(task);

        manager.getTask(id);

        Task copy = historyManager.getHistory().get(0);

        Assertions.assertNotSame(task, copy, "Менеджер должен возвращать копию, а не оригинал.");

        copy.setId(id + 100);
        copy.setName("Новое имя");
        copy.setDescription("Новое описание");
        copy.setStatus(Status.DONE);

        Task fromHistory = historyManager.getHistory().get(0);
        Assertions.assertEquals(id, fromHistory.getId(), "Айди задачи из истории было изменено.");
        Assertions.assertEquals("Таск 1", fromHistory.getName(), "Имя задачи из истории было изменено.");
        Assertions.assertEquals("Описание таска 1", fromHistory.getDescription(), "Описание задачи из истории было изменено.");
        Assertions.assertEquals(Status.NEW, fromHistory.getStatus(), "Статус задачи из истории был изменен.");

        historyManager.remove(id);

        Assertions.assertTrue(historyManager.getHistory().isEmpty(), "Задача не была удалена по своему айди.");
    }

    @Test
    void changingSubtaskCopyShouldNotAffectHistoryManager() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic);

        Subtask subtask = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID);
        int subtaskID = manager.createSubtask(subtask);

        manager.getSubtask(subtaskID);

        Subtask copy = (Subtask) historyManager.getHistory().get(0);

        Assertions.assertNotSame(subtask, copy, "Менеджер должен возвращать копию, а не оригинал.");

        copy.setId(subtaskID + 100);
        copy.setName("Новое имя");
        copy.setDescription("Новое описание");
        copy.setStatus(Status.DONE);

        Subtask fromHistory = (Subtask) historyManager.getHistory().get(0);
        Assertions.assertEquals(subtaskID, fromHistory.getId(), "Айди подзадачи из истории было изменено.");
        Assertions.assertEquals("Сабтаск 1", fromHistory.getName(), "Имя подзадачи из истории было изменено.");
        Assertions.assertEquals("Сабтаск эпика 1", fromHistory.getDescription(), "Описание подзадачи из истории было изменено.");
        Assertions.assertEquals(Status.NEW, fromHistory.getStatus(), "Статус подзадачи из истории был изменен.");

        historyManager.remove(subtaskID);

        Assertions.assertTrue(historyManager.getHistory().isEmpty(), "Задача не была удалена по своему айди.");
    }

    @Test
    void changingSubtaskCopyShouldNotAffectEpicInHistoryManager() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic);
        Status originalStatus = epic.getStatus();

        Subtask subtask = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID);
        int subtaskID = manager.createSubtask(subtask);

        manager.getSubtask(subtaskID);
        manager.getEpic(epicID);

        Subtask copy = (Subtask) historyManager.getHistory().get(1);
        copy.setStatus(Status.DONE);

        Epic fromHistory = (Epic) historyManager.getHistory().get(0);
        Assertions.assertEquals(originalStatus, fromHistory.getStatus());

    }

    @Test
    void changingEpicCopyShouldNotAffectHistoryManager() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int id = manager.createEpic(epic);
        List<Integer> originalSubtaskIds = epic.getSubtaskIDs();
        Status originalStatus = epic.getStatus();

        manager.getEpic(id);

        Epic copy = (Epic) historyManager.getHistory().get(0);

        Assertions.assertNotSame(epic, copy, "Менеджер должен возвращать копию, а не оригинал.");

        copy.setId(id + 100);
        copy.setName("Новое имя");
        copy.setDescription("Новое описание");
        copy.setStatus(Status.DONE);
        epic.setSubtaskIDs(List.of(1, 2, 3));

        Epic fromHistory = (Epic) historyManager.getHistory().get(0);
        Assertions.assertEquals(id, fromHistory.getId(), "Айди эпика из истории было изменено.");
        Assertions.assertEquals("Эпик 1", fromHistory.getName(), "Имя эпика из истории было изменено.");
        Assertions.assertEquals("Описание эпика 1", fromHistory.getDescription(), "Описание эпика из истории было изменено.");
        Assertions.assertEquals(originalStatus, fromHistory.getStatus(), "Статус эпика из истории был изменен.");
        Assertions.assertEquals(originalSubtaskIds, fromHistory.getSubtaskIDs(), "Статус эпика из истории был изменен.");

        historyManager.remove(id);

        Assertions.assertTrue(historyManager.getHistory().isEmpty(), "Эпик не был удален по своему айди.");
    }

    @Test
    void changingOriginalTaskShouldNotAffectHistoryManager() {
        Task task = new Task("Таск 1", "Описание таска 1", Status.NEW);
        int id = manager.createTask(task);

        manager.getTask(id);

        task.setId(id + 100);
        task.setName("Новое имя");
        task.setDescription("Новое описание");
        task.setStatus(Status.DONE);

        Task fromHistory = historyManager.getHistory().get(0);
        Assertions.assertEquals(id, fromHistory.getId(), "Айди задачи из истории было изменено.");
        Assertions.assertEquals("Таск 1", fromHistory.getName(), "Имя задачи из истории было изменено.");
        Assertions.assertEquals("Описание таска 1", fromHistory.getDescription(), "Описание задачи из истории было изменено.");
        Assertions.assertEquals(Status.NEW, fromHistory.getStatus(), "Статус задачи из истории был изменен.");

    }

    @Test
    void changingOriginalSubtaskShouldNotAffectHistoryManager() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic);

        Subtask subtask = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID);
        int subtaskID = manager.createSubtask(subtask);

        manager.getSubtask(subtaskID);

        subtask.setId(subtaskID + 100);
        subtask.setName("Новое имя");
        subtask.setDescription("Новое описание");
        subtask.setStatus(Status.DONE);

        Task fromHistory = historyManager.getHistory().get(0);
        Assertions.assertEquals(subtaskID, fromHistory.getId(), "Айди подзадачи из истории было изменено.");
        Assertions.assertEquals("Сабтаск 1", fromHistory.getName(), "Имя подзадачи из истории было изменено.");
        Assertions.assertEquals("Сабтаск эпика 1", fromHistory.getDescription(), "Описание подзадачи из истории было изменено.");
        Assertions.assertEquals(Status.NEW, fromHistory.getStatus(), "Статус подзадачи из истории был изменен.");

    }

    @Test
    void changingOriginalSubtaskShouldNotAffectEpicInHistoryManager() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic);
        Status originalStatus = epic.getStatus();

        Subtask subtask = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID);
        int subtaskID = manager.createSubtask(subtask);

        manager.getSubtask(subtaskID);
        manager.getEpic(epicID);

        subtask.setStatus(Status.DONE);

        Epic fromHistory = (Epic) historyManager.getHistory().get(0);
        Assertions.assertEquals(originalStatus, fromHistory.getStatus());

    }

    @Test
    void changingOriginalEpicShouldNotAffectHistoryManager() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int id = manager.createEpic(epic);
        List<Integer> originalSubtaskIds = epic.getSubtaskIDs();
        Status originalStatus = epic.getStatus();

        manager.getEpic(id);

        epic.setId(id + 100);
        epic.setName("Новое имя");
        epic.setDescription("Новое описание");
        epic.setStatus(Status.DONE);
        epic.setSubtaskIDs(List.of(1, 2, 3));

        Epic fromHistory = (Epic) historyManager.getHistory().get(0);
        Assertions.assertEquals(id, fromHistory.getId(), "Айди эпика из истории было изменено.");
        Assertions.assertEquals("Эпик 1", fromHistory.getName(), "Имя эпика из истории было изменено.");
        Assertions.assertEquals("Описание эпика 1", fromHistory.getDescription(), "Описание эпика из истории было изменено.");
        Assertions.assertEquals(Status.NEW, fromHistory.getStatus(), "Статус эпика из истории был изменен.");
        Assertions.assertEquals(originalSubtaskIds, fromHistory.getSubtaskIDs(), "Статус эпика из истории был изменен.");

    }

}