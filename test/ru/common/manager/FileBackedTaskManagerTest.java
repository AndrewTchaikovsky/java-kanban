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
import java.io.IOException;
import java.nio.file.Files;

public class FileBackedTaskManagerTest {
    private static FileBackedTaskManager manager;
    private static HistoryManager historyManager;
    private static File tempFile;

    @BeforeAll
    static void setUp() throws IOException {
        tempFile = File.createTempFile("tempFile", ".csv");
        manager = (FileBackedTaskManager) Managers.getDefault(tempFile);
        historyManager = manager.getHistoryManager();
    }

    @BeforeEach
    void beforeEachTest() throws IOException {
        tempFile = File.createTempFile("tempFile", ".csv");
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        historyManager.clearHistory();
    }

    @Test
    void shouldSaveAndLoadEmptyManager() {
        manager.save();

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(tempFile);

        Assertions.assertTrue(newManager.getTasks().isEmpty());
        Assertions.assertTrue(newManager.getSubtasks().isEmpty());
        Assertions.assertTrue(newManager.getEpics().isEmpty());
        Assertions.assertTrue(newManager.getHistoryManager().getHistory().isEmpty());
        Assertions.assertEquals(1, FileBackedTaskManager.id);
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

        Assertions.assertEquals(manager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(manager.getSubtasks(), newManager.getSubtasks());
        Assertions.assertEquals(manager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(manager.getHistoryManager().getHistory(), newManager.getHistoryManager().getHistory());

    }

    @Test
    void shouldNotLoadFromEmptyFile() throws IOException {
        FileBackedTaskManager.loadFromFile(tempFile);

        System.out.println(Files.readString(tempFile.toPath()));

        Assertions.assertTrue(manager.getTasks().isEmpty());
        Assertions.assertTrue(manager.getSubtasks().isEmpty());
        Assertions.assertTrue(manager.getEpics().isEmpty());
        Assertions.assertTrue(manager.getHistoryManager().getHistory().isEmpty());

    }


}
