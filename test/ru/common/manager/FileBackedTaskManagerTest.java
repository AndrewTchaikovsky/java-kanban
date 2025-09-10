package ru.common.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.common.model.Epic;
import ru.common.model.Status;
import ru.common.model.Subtask;
import ru.common.model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileBackedTaskManagerTest {
    private static FileBackedTaskManager manager;
    private static HistoryManager historyManager;
    private static File tempFile;

    @BeforeEach
    void beforeEachTest() throws IOException {
        tempFile = File.createTempFile("tempFile", ".csv");
        manager = (FileBackedTaskManager) Managers.getDefault(tempFile);
        historyManager = manager.getHistoryManager();
    }

    @Test
    void shouldSaveAndLoadEmptyManager() {
        manager.save();

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(tempFile);

        Assertions.assertTrue(newManager.getTasks().isEmpty(), "Задачи не пустые.");
        Assertions.assertTrue(newManager.getSubtasks().isEmpty(), "Подзадачи не пустые.");
        Assertions.assertTrue(newManager.getEpics().isEmpty(), "Эпики не пустые.");
        Assertions.assertTrue(newManager.getHistoryManager().getHistory().isEmpty(), "История не пустая.");
        Assertions.assertEquals(1, FileBackedTaskManager.id, "Айди начинается не с единицы.");
    }

    @Test
    void shouldCorrectlySaveAndLoadTasksToFile() throws IOException {
        Task task1 = new Task("Таск 1", "Описание таска 1", Status.NEW);
        Task task2 = new Task("Таск 2", "Описание таска 2", Status.NEW);
        int task1ID = manager.createTask(task1);
        int task2ID = manager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Эпик с 3 подзадачами");
        Epic epic2 = new Epic("Эпик 2", "Эпика без подзадач");
        int epic1ID = manager.createEpic(epic1);
        int epic2ID = manager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Сабтаск 2", "Сабтаск эпика 1", Status.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("Сабтаск 3", "Сабтаск эпика 1", Status.NEW, epic1.getId());
        int subtask1ID = manager.createSubtask(subtask1);
        int subtask2ID = manager.createSubtask(subtask2);
        int subtask3ID = manager.createSubtask(subtask3);

        manager.getTask(task1ID);
        manager.getTask(task2ID);
        manager.getEpic(epic1ID);
        manager.getEpic(epic2ID);
        manager.getSubtask(subtask1ID);
        manager.getSubtask(subtask2ID);
        manager.getSubtask(subtask3ID);

        System.out.println(Files.readString(tempFile.toPath()));

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(tempFile);

        Assertions.assertEquals(manager.getTasks(), newManager.getTasks(), "Задачи в восстановленном менеджере не пустые.");
        Assertions.assertEquals(manager.getSubtasks(), newManager.getSubtasks(), "Подзадачи в восстановленном менеджере не пустые.");
        Assertions.assertEquals(manager.getEpics(), newManager.getEpics(), "Эпики в восстановленном менеджере не пустые.");
        Assertions.assertEquals(manager.getHistoryManager().getHistory(), newManager.getHistoryManager().getHistory(), "История в восстановленном менеджере не пустая.");

    }

    @Test
    void shouldNotLoadFromEmptyFile() throws IOException {
        File emptyFile = File.createTempFile("tempFile", ".csv");
        FileBackedTaskManager.loadFromFile(emptyFile);

        System.out.println(Files.readString(emptyFile.toPath()));

        Assertions.assertTrue(manager.getTasks().isEmpty(), "Задачи не пустые.");
        Assertions.assertTrue(manager.getSubtasks().isEmpty(), "Подзадачи не пустые.");
        Assertions.assertTrue(manager.getEpics().isEmpty(), "Эпики не пустые.");
        Assertions.assertTrue(manager.getHistoryManager().getHistory().isEmpty(), "История не пустая.");
        Assertions.assertEquals(1, FileBackedTaskManager.id, "Айди начинается не с единицы.");

    }

    @Test
    void iDMustStayConsistent() throws IOException {
        Task task1 = new Task("Таск 1", "Описание таска 1", Status.NEW);
        manager.createTask(task1);

        Epic epic1 = new Epic("Эпик 1", "Эпик с 3 подзадачами");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epic1.getId());
        int subtask1ID = manager.createSubtask(subtask1);

        System.out.println(Files.readString(tempFile.toPath()));

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(tempFile);

        Task task2 = new Task("Таск 2", "Описание таска 2", Status.NEW);
        int task2ID = newManager.createTask(task2);

        Epic epic2 = new Epic("Эпик 2", "Эпика без подзадач");
        int epic2ID = newManager.createEpic(epic2);

        Subtask subtask2 = new Subtask("Сабтаск 2", "Сабтаск эпика 1", Status.NEW, epic1.getId());
        int subtask2ID = newManager.createSubtask(subtask2);

        Assertions.assertEquals(subtask1ID + 1, task2ID, "Айди задачи не последовательный");
        Assertions.assertEquals(subtask1ID + 2, epic2ID, "Айди подзадачи не последовательный");
        Assertions.assertEquals(subtask1ID + 3, subtask2ID, "Айди эпика не последовательный");
    }

    @Test
    void shouldLoadEmptyManagerWhenTasksAreDeleted() throws IOException {
        Task task1 = new Task("Таск 1", "Описание таска 1", Status.NEW);
        int task1ID = manager.createTask(task1);

        Epic epic1 = new Epic("Эпик 1", "Эпик с 3 подзадачами");
        int epic1ID = manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epic1.getId());
        int subtask1ID = manager.createSubtask(subtask1);

        manager.getTask(task1ID);
        manager.getEpic(epic1ID);
        manager.getSubtask(subtask1ID);

        System.out.println(Files.readString(tempFile.toPath()));

        manager.deleteTasks();
        manager.deleteEpics();
        manager.deleteSubtasks();

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(tempFile);

        Assertions.assertTrue(newManager.getTasks().isEmpty(), "Задачи не пустые.");
        Assertions.assertTrue(newManager.getSubtasks().isEmpty(), "Подзадачи не пустые.");
        Assertions.assertTrue(newManager.getEpics().isEmpty(), "Эпики не пустые.");
        Assertions.assertTrue(newManager.getHistoryManager().getHistory().isEmpty(), "История не пустая.");
        Assertions.assertEquals(1, FileBackedTaskManager.id, "Айди начинается не с единицы.");
    }

    @Test
    void invalidFileShouldThrowException() {

        try {
            FileBackedTaskManager.loadFromFile(new File(""));
            Assertions.fail("ManagerSaveException не было брошено.");
        } catch (ManagerSaveException e) {
            Assertions.assertTrue(e.getMessage().contains("Can't load file"));
        }

    }


}
