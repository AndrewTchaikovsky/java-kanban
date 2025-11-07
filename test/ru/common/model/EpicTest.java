package ru.common.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.common.manager.InMemoryTaskManager;
import ru.common.manager.Managers;
import ru.common.manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    private static TaskManager manager;

    @BeforeAll
    static void setUp() {
        manager = Managers.getDefault();
    }

    @Test
    void epicObjectsAreEqualIftheirIDsAreEqual() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epic1ID = manager.createEpic(epic1);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        InMemoryTaskManager.id = epic1ID;
        manager.createEpic(epic2);
        System.out.println(epic1);
        System.out.println(epic2);
        assertEquals(epic1, epic2, "Объекты Epic не равны друг другу если равен их ID");
    }

    @Test
    void shouldNotAllowEpicToBeItsOwnSubtask() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic1);
        InMemoryTaskManager.id = epicID;
        Subtask subtask1 = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID, null, Duration.ZERO);
        int subtaskID = manager.createSubtask(subtask1);

        assertEquals(0, subtaskID, "Эпик нельзя добавить в самого себя в виде подзадачи");
    }

    @Test
    public void shouldSetStatusNewIfAllSubtasksNew() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epic1.getId(), LocalDateTime.now(), Duration.ZERO);
        Subtask subtask2 = new Subtask("Сабтаск 2", "Сабтаск эпика 1", Status.NEW, epic1.getId(), LocalDateTime.now(), Duration.ZERO);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Assertions.assertEquals(Status.NEW, epic1.getStatus(), "Все подзадачи со статусом NEW, значит статус эпика должен быть NEW");
    }

    @Test
    public void shouldSetStatusDoneIfAllSubtasksDone() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.DONE, epic1.getId(), LocalDateTime.now(), Duration.ZERO);
        Subtask subtask2 = new Subtask("Сабтаск 2", "Сабтаск эпика 1", Status.DONE, epic1.getId(), LocalDateTime.now(), Duration.ZERO);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Assertions.assertEquals(Status.DONE, epic1.getStatus(), "Все подзадачи со статусом DONE, значит статус эпика должен быть DONE");
    }

    @Test
    public void shouldSetStatusInProgressIfNewAndDone() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epic1.getId(), LocalDateTime.now(), Duration.ZERO);
        Subtask subtask2 = new Subtask("Сабтаск 2", "Сабтаск эпика 1", Status.DONE, epic1.getId(), LocalDateTime.now(), Duration.ZERO);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Assertions.assertEquals(Status.IN_PROGRESS, epic1.getStatus(), "Подзадачи со статусом NEW и DONE, значит статус эпика должен быть IN_PROGRESS");
    }

    @Test
    public void shouldSetStatusInProgressIfAnySubtaskInProgress() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.DONE, epic1.getId(), LocalDateTime.now(), Duration.ZERO);
        Subtask subtask2 = new Subtask("Сабтаск 2", "Сабтаск эпика 1", Status.IN_PROGRESS, epic1.getId(), LocalDateTime.now(), Duration.ZERO);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Assertions.assertEquals(Status.IN_PROGRESS, epic1.getStatus(), "Хотя бы одна подзадача со статусом IN_PROGRESS, значит статус эпика должен быть IN_PROGRESS");
    }

}