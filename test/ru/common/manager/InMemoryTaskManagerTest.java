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

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private static TaskManager manager;

    @BeforeAll
    static void setUp() {
        manager = Managers.getDefault();
    }

    @BeforeEach
    void beforeEachTest() {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
    }

    @Test
    void shouldCreateTaskAndFindIt() {
        Task task1 = new Task("Таск 1", "Описание таска 1", Status.NEW);
        int taskID = manager.createTask(task1);

        assertNotNull(task1, "Задача должна быть создана.");
        assertEquals(manager.getTask(taskID), task1, "Задачу не получается найти по айди");
    }

    @Test
    void shouldCreateEpicAndFindIt() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic1);

        assertNotNull(epic1, "Эпик должен быть создан.");
        assertEquals(manager.getEpic(epicID), epic1, "Эпик не получается найти по айди");
    }

    @Test
    void shouldCreateSubtaskAndFindIt() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epic1.getId());
        int subtaskID = manager.createSubtask(subtask1);

        assertNotNull(subtask1, "Подзадача должна быть создана.");
        assertEquals(manager.getSubtask(subtaskID), subtask1, "Подзадачу не получается найти по айди");
    }

    @Test
    void shouldNotConflictWithManuallySetID() {
        Task task1 = new Task("Таск 1", "Описание таска 1", Status.NEW);
        Task task2 = new Task("Таск 2", "Описание таска 2", Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        Task task3 = new Task(task2.getId() + 1, "Таск 3", "Описание таска 3", Status.NEW);
        manager.createTask(task3);

        Task task4 = new Task("Таск 4", "Описание таска 4", Status.NEW);
        manager.createTask(task4);

        assertNotEquals(task3.getId(), task4.getId(), "Задача с заданным айди конфликтует с задачей с автосгенерированным айди.");
    }

    @Test
    void taskFieldsShouldNotChangeWhenTaskIsCreated() {
        Task task = new Task("Таск 1", "Описание таска 1", Status.NEW);
        int taskID = manager.createTask(task);
        Task retrievedTask = manager.getTask(taskID);

        assertEquals(task.getName(), retrievedTask.getName(), "Название задачи отличается после ее создания.");
        assertEquals(task.getDescription(), retrievedTask.getDescription(), "Описание задачи отличается после ее создания.");
        assertEquals(task.getStatus(), retrievedTask.getStatus(), "Статус задачи отличается после ее создания.");
    }

    @Test
    void shouldDeleteAllTasks() {
        Task task1 = new Task("Таск 1", "Описание таска 1", Status.NEW);
        Task task2 = new Task("Таск 2", "Описание таска 2", Status.NEW);
        int task1ID = manager.createTask(task1);
        int task2ID = manager.createTask(task2);

        assertEquals(manager.getTask(task1ID), task1);
        assertEquals(manager.getTask(task2ID), task2);

        manager.deleteTasks();
        assertTrue(manager.getTasks().isEmpty(), "Не все задачи были удалены.");
    }

    @Test
    void shouldUpdateAllFieldsOfTaskCorrectly() {
        Task task = new Task("Таск 1", "Описание таска 1", Status.NEW);
        int taskID = manager.createTask(task);

        Task updatedTask = new Task(taskID, "Таск 2", "Описание таска 2", Status.DONE);
        manager.updateTask(updatedTask);

        assertEquals(taskID, updatedTask.getId(), "Айди задачи не совпадает после обновления.");
        assertEquals("Таск 2", updatedTask.getName(), "Название задачи не совпадает после обновления.");
        assertEquals("Описание таска 2", updatedTask.getDescription(), "Описание задачи не совпадает после обновления.");
        assertEquals(Status.DONE, updatedTask.getStatus(), "Статус задачи не совпадает после обновления.");
    }

    @Test
    void shouldDeleteTask() {
        Task task = new Task("Таск 1", "Описание таска 1", Status.NEW);
        int taskID = manager.createTask(task);

        assertNotNull(manager.getTask(taskID), "Задача не была создана.");

        manager.deleteTask(taskID);
        assertNull(manager.getTask(taskID), "Задача не была удалена.");
    }

    @Test
    void shouldReturnCorrectListOfTasks() {
        Task task1 = new Task("Таск 1", "Описание таска 1", Status.NEW);
        Task task2 = new Task("Таск 2", "Описание таска 2", Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> expected = new ArrayList<>();
        expected.add(task1);
        expected.add(task2);

        List<Task> actual = new ArrayList<>(manager.getTasks());

        assertEquals(expected, actual, "Список задач не совпадает с добавленными задачами.");
    }

    @Test
    void shouldReturnCorrectListOfSubtasks() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID);
        Subtask subtask2 = new Subtask("Сабтаск 2", "Сабтаск эпика 1", Status.NEW, epicID);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        List<Task> expected = new ArrayList<>();
        expected.add(subtask1);
        expected.add(subtask2);

        List<Task> actual = new ArrayList<>(manager.getSubtasks());

        assertEquals(expected, actual, "Список подзадач не совпадает с добавленными подзадачами.");
    }

    @Test
    void shouldDeleteSubtask() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic1);

        Subtask subtask = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID);
        int subtaskID = manager.createSubtask(subtask);

        assertNotNull(manager.getSubtask(subtaskID), "Подзадача не была создана.");

        manager.deleteSubtask(subtaskID);
        assertNull(manager.getSubtask(subtaskID), "Подзадача не была удалена.");
    }

    @Test
    void shouldUpdateAllFieldsOfSubtaskCorrectly() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic1);

        Subtask subtask = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID);
        int subtaskID = manager.createSubtask(subtask);

        Subtask updatedSubtask = new Subtask(subtaskID, "Сабтаск 2", "Сабтаск эпика 2", Status.DONE, epicID);
        manager.updateSubtask(updatedSubtask);

        assertEquals(subtaskID, updatedSubtask.getId(), "Айди подзадачи не совпадает после обновления.");
        assertEquals("Сабтаск 2", updatedSubtask.getName(), "Название подзадачи не совпадает после обновления.");
        assertEquals("Сабтаск эпика 2", updatedSubtask.getDescription(), "Описание подзадачи не совпадает после обновления.");
        assertEquals(Status.DONE, updatedSubtask.getStatus(), "Статус подзадачи не совпадает после обновления.");
    }

    @Test
    void shouldDeleteAllSubtasks() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID);
        Subtask subtask2 = new Subtask("Сабтаск 2", "Сабтаск эпика 1", Status.NEW, epicID);
        int subtask1ID = manager.createSubtask(subtask1);
        int subtask2ID = manager.createSubtask(subtask2);

        assertEquals(manager.getSubtask(subtask1ID), subtask1);
        assertEquals(manager.getSubtask(subtask2ID), subtask2);

        manager.deleteSubtasks();
        assertTrue(manager.getSubtasks().isEmpty(), "Не все подзадачи были удалены.");
    }

    @Test
    void shouldReturnCorrectListOfEpics() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        List<Task> expected = new ArrayList<>();
        expected.add(epic1);
        expected.add(epic2);

        List<Task> actual = new ArrayList<>(manager.getEpics());

        Assertions.assertTrue(expected.containsAll(actual) && actual.containsAll(expected), "Список эпиков не совпадает с добавленными эпиками.");
    }

    @Test
    void shouldDeleteEpic() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic);

        assertNotNull(manager.getEpic(epicID), "Эпик не был создан.");

        manager.deleteEpic(epicID);
        assertNull(manager.getEpic(epicID), "Эпик не был удален.");
    }

    @Test
    void shouldDeleteAllEpics() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        int epic1ID = manager.createEpic(epic1);
        int epic2ID = manager.createEpic(epic2);

        assertEquals(manager.getEpic(epic1ID), epic1);
        assertEquals(manager.getEpic(epic2ID), epic2);

        manager.deleteEpics();
        assertTrue(manager.getEpics().isEmpty(), "Не все эпики были удалены.");
    }

    @Test
    void shouldUpdateAllFieldsOfEpicCorrectly() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic);

        Epic updatedEpic = new Epic(epicID, "Эпик 2", "Описание эпика 2");
        manager.updateEpic(updatedEpic);

        assertEquals(epicID, updatedEpic.getId(), "Айди эпика не совпадает после обновления.");
        assertEquals("Эпик 2", updatedEpic.getName(), "Название эпика не совпадает после обновления.");
        assertEquals("Описание эпика 2", updatedEpic.getDescription(), "Описание эпика не совпадает после обновления.");
    }

    @Test
    void shouldCalculateEpicStatusCorrectly() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic);

        Subtask subtask = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID);
        int subtaskID = manager.createSubtask(subtask);

        Epic originalFromManager = manager.getEpic(epicID);

        assertEquals(Status.NEW, originalFromManager.getStatus(), "Статус эпика не совпадает со статусом подзадачи.");

        Subtask updatedSubtask = new Subtask(subtaskID, "Сабтаск 1", "Сабтаск эпика 1", Status.DONE, epicID);
        manager.updateSubtask(updatedSubtask);

        Epic updatedFromManager = manager.getEpic(epicID);

        assertEquals(Status.DONE, updatedFromManager.getStatus(), "Статус эпика не совпадает со статусом подзадачи.");
    }

    @Test
    void shouldReturnCorrectSubtaskIDs() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID);
        Subtask subtask2 = new Subtask("Сабтаск 2", "Сабтаск эпика 1", Status.NEW, epicID);
        int subtask1ID = manager.createSubtask(subtask1);
        int subtask2ID = manager.createSubtask(subtask2);

        List<Integer> expected = new ArrayList<>();
        expected.add(subtask1ID);
        expected.add(subtask2ID);

        List<Integer> actual = new ArrayList<>(manager.getEpicSubtaskIDs(epicID));

        assertEquals(expected, actual, "Список айди подзадач не совпадает с айди добавленных подзадач.");
    }

    @Test
    void shouldReturnCorrectListOfEpicSubtasks() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID);
        Subtask subtask2 = new Subtask("Сабтаск 2", "Сабтаск эпика 1", Status.NEW, epicID);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        List<Task> expected = new ArrayList<>();
        expected.add(subtask1);
        expected.add(subtask2);

        List<Task> actual = new ArrayList<>(manager.getEpicSubtasks(epicID));

        assertEquals(expected, actual, "Список подзадач эпика не совпадает с добавленными в этот эпик подзадачами.");
    }

    @Test
    void deletedSubtasksShouldBeRemovedFromTheMap() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic);

        Subtask subtask = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID);
        int subtaskID = manager.createSubtask(subtask);

        Assertions.assertNotNull(manager.getSubtask(subtaskID), "Подзадача не была добавлена.");

        manager.deleteSubtask(subtaskID);
        Assertions.assertNull(manager.getSubtask(subtaskID), "Подзадача не была удалена.");
    }

    @Test
    void epicShouldNotContainDeletedSubtasksIds() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic);

        Subtask subtask = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID);
        int subtaskID = manager.createSubtask(subtask);

        Epic fromManager = manager.getEpic(epicID);

        Assertions.assertTrue(fromManager.getSubtaskIDs().contains(subtaskID), "Айди подзадачи не было добавлено в эпик.");

        manager.deleteSubtask(subtaskID);

        Epic fromManagerAfterDeletion = manager.getEpic(epicID);

        Assertions.assertFalse(fromManagerAfterDeletion.getSubtaskIDs().contains(subtaskID), "Айди удаленной подзадачи не было удалено из эпика.");
    }

    @Test
    void historyManagerShouldNotKeepDeletedSubtasks() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic);

        Subtask subtask = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID);
        int subtaskID = manager.createSubtask(subtask);

        manager.getSubtask(subtaskID);
        Assertions.assertTrue(manager.getHistoryManager().getHistory().contains(subtask));

        manager.deleteSubtask(subtaskID);
        Assertions.assertFalse(manager.getHistoryManager().getHistory().contains(subtask));

    }

    @Test
    void changingOriginalTaskShouldNotAffectManager() {
        Task task = new Task("Таск 1", "Описание таска 1", Status.NEW);
        int id = manager.createTask(task);

        task.setId(id + 100);
        task.setName("Новое имя");
        task.setDescription("Новое описание");
        task.setStatus(Status.DONE);

        Task fromManager = manager.getTask(id);
        Assertions.assertNotNull(fromManager, "Задача не найдена по оригинальному айди.");
        Assertions.assertEquals(id, fromManager.getId(), "Айди задачи в менеджере был изменен.");
        Assertions.assertEquals("Таск 1", fromManager.getName(), "Имя задачи в менеджере было изменено.");
        Assertions.assertEquals("Описание таска 1", fromManager.getDescription(), "Описание задачи в менеджере было изменено.");
        Assertions.assertEquals(Status.NEW, fromManager.getStatus(), "Статус задачи в менеджере был изменен.");

        Assertions.assertNull(manager.getTask(id + 100), "В менеджере не хранится задача с новым айди.");
    }

    @Test
    void changingOriginalSubtaskShouldNotAffectManager() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic);

        Subtask subtask = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID);
        int subtaskID = manager.createSubtask(subtask);

        subtask.setId(subtaskID + 100);
        subtask.setName("Новое имя");
        subtask.setDescription("Новое описание");
        subtask.setStatus(Status.DONE);

        Subtask fromManager = manager.getSubtask(subtaskID);
        Assertions.assertNotNull(fromManager, "Подзадача не найдена по оригинальному айди.");
        Assertions.assertEquals(subtaskID, fromManager.getId(), "Айди подзадачи в менеджере был изменен.");
        Assertions.assertEquals("Сабтаск 1", fromManager.getName(), "Имя подзадачи в менеджере было изменено.");
        Assertions.assertEquals("Сабтаск эпика 1", fromManager.getDescription(), "Описание подзадачи в менеджере было изменено.");
        Assertions.assertEquals(Status.NEW, fromManager.getStatus(), "Статус подзадачи в менеджере был изменен.");

        Assertions.assertNull(manager.getSubtask(subtaskID + 100), "В менеджере не хранится подзадача с новым айди.");
    }

    @Test
    void changingOriginalSubtaskShouldNotAffectEpicInManager() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic);
        Status originalStatus = epic.getStatus();

        Subtask subtask = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID);
        manager.createSubtask(subtask);

        subtask.setStatus(Status.DONE);

        Epic fromManager = manager.getEpic(epicID);
        Assertions.assertEquals(originalStatus, fromManager.getStatus());

    }

    @Test
    void changingOriginalEpicShouldNotAffectManager() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int id = manager.createEpic(epic);
        List<Integer> originalSubtaskIds = epic.getSubtaskIDs();
        Status originalStatus = epic.getStatus();

        epic.setId(id + 100);
        epic.setName("Новое имя");
        epic.setDescription("Новое описание");
        epic.setStatus(Status.DONE);
        epic.setSubtaskIDs(List.of(1, 2, 3));

        Epic fromManager = manager.getEpic(id);
        Assertions.assertNotNull(fromManager, "Эпик не найден по оригинальному айди.");
        Assertions.assertEquals(id, fromManager.getId(), "Айди эпика в менеджере был изменено.");
        Assertions.assertEquals("Эпик 1", fromManager.getName(), "Имя эпика в менеджере было изменено.");
        Assertions.assertEquals("Описание эпика 1", fromManager.getDescription(), "Описание эпика в менеджере было изменено.");
        Assertions.assertEquals(originalStatus, fromManager.getStatus(), "Статус эпика в менеджере был изменен.");
        Assertions.assertEquals(originalSubtaskIds, fromManager.getSubtaskIDs(), "Статус эпика в менеджере был изменен.");

        Assertions.assertNull(manager.getEpic(id + 100), "В менеджере не хранится эпик с новым айди.");
    }

    @Test
    void changingTaskCopyShouldNotAffectManager() {
        Task task = new Task("Таск 1", "Описание таска 1", Status.NEW);
        int id = manager.createTask(task);

        Task copy = manager.getTask(id);

        Assertions.assertNotSame(task, copy, "Менеджер должен возвращать копию, а не оригинал.");

        copy.setId(id + 100);
        copy.setName("Новое имя");
        copy.setDescription("Новое описание");
        copy.setStatus(Status.DONE);

        Task fromManager = manager.getTask(id);
        Assertions.assertEquals(id, fromManager.getId(), "Айди задачи из списка задач было изменено.");
        Assertions.assertEquals("Таск 1", fromManager.getName(), "Имя задачи из списка задач было изменено.");
        Assertions.assertEquals("Описание таска 1", fromManager.getDescription(), "Описание задачи из списка задач было изменено.");
        Assertions.assertEquals(Status.NEW, fromManager.getStatus(), "Статус задачи из списка задач был изменен.");
    }

    @Test
    void changingSubtaskCopyShouldNotAffectManager() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic);
        Status originalStatus = epic.getStatus();

        Subtask subtask = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID);
        int subtaskID = manager.createSubtask(subtask);

        Subtask copy = manager.getSubtask(subtaskID);

        Assertions.assertNotSame(subtask, copy, "Менеджер должен возвращать копию, а не оригинал.");

        copy.setId(subtaskID + 100);
        copy.setName("Новое имя");
        copy.setDescription("Новое описание");
        copy.setStatus(Status.DONE);

        Subtask fromManager = manager.getSubtask(subtaskID);
        Assertions.assertEquals(subtaskID, fromManager.getId(), "Айди подзадачи из списка задач было изменено.");
        Assertions.assertEquals("Сабтаск 1", fromManager.getName(), "Имя подзадачи из списка задач было изменено.");
        Assertions.assertEquals("Сабтаск эпика 1", fromManager.getDescription(), "Описание подзадачи из списка задач было изменено.");
        Assertions.assertEquals(originalStatus, fromManager.getStatus(), "Статус подзадачи из списка задач был изменен.");
    }

    @Test
    void changingSubtaskCopyShouldNotAffectEpicInManager() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic);
        Status originalStatus = epic.getStatus();

        Subtask subtask = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID);
        int subtaskID = manager.createSubtask(subtask);

        Subtask copy = manager.getSubtask(subtaskID);
        copy.setStatus(Status.DONE);

        Epic fromManager = manager.getEpic(epicID);
        Assertions.assertEquals(originalStatus, fromManager.getStatus());

    }

    @Test
    void changingEpicCopyShouldNotAffectManager() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int id = manager.createEpic(epic);
        List<Integer> originalSubtaskIds = epic.getSubtaskIDs();
        Status originalStatus = epic.getStatus();

        Epic copy = manager.getEpic(id);

        Assertions.assertNotSame(epic, copy, "Менеджер должен возвращать копию, а не оригинал.");

        copy.setId(id + 100);
        copy.setName("Новое имя");
        copy.setDescription("Новое описание");
        copy.setStatus(Status.DONE);
        epic.setSubtaskIDs(List.of(1, 2, 3));

        Epic fromManager = manager.getEpic(id);
        Assertions.assertEquals(id, fromManager.getId(), "Айди эпика из списка задач было изменено.");
        Assertions.assertEquals("Эпик 1", fromManager.getName(), "Имя эпика из списка задач было изменено.");
        Assertions.assertEquals("Описание эпика 1", fromManager.getDescription(), "Описание эпика из списка задач было изменено.");
        Assertions.assertEquals(originalStatus, fromManager.getStatus(), "Статус эпика из списка задач был изменен.");
        Assertions.assertEquals(originalSubtaskIds, fromManager.getSubtaskIDs(), "Статус эпика в менеджере был изменен.");
    }

}