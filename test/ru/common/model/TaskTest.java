package ru.common.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.common.manager.HistoryManager;
import ru.common.manager.Managers;
import ru.common.manager.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private static TaskManager manager;
    private static HistoryManager historyManager;

    @BeforeAll
    static void setUp() {
        manager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void taskObjectsAreEqualIftheirIDsAreEqual() {
        Task task1 = new Task(15, "Таск 1", "Описание таска 1", Status.NEW);
        Task task2 = new Task(15, "Таск 2", "Описание таска 2", Status.IN_PROGRESS);

        assertEquals(task1, task2, "Объекты Task не равны друг другу если равен их ID");
    }

    @Test
    void subtaskObjectsAreEqualIftheirIDsAreEqual() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Subtask subtask1 = new Subtask(120, "Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(120, "Сабтаск 2", "Сабтаск эпика 2", Status.IN_PROGRESS, epic1.getId());

        assertEquals(subtask1, subtask2, "Объекты Subtask не равны друг другу если равен их ID");
    }

    @Test
    void epicObjectsAreEqualIftheirIDsAreEqual() {
        Epic epic1 = new Epic(22, "Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic(22, "Эпик 2", "Описание эпика 2");

        assertEquals(epic1, epic2, "Объекты Epic не равны друг другу если равен их ID");
    }

    @Test
    void shouldNotAllowEpicToBeItsOwnSubtask() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic1);
        Subtask subtask1 = new Subtask(epic1.getId(), "Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epic1.getId());
        int subtaskID = manager.createSubtask(subtask1);

        assertFalse(manager.getEpic(epicID).getSubtaskIDs().contains(epicID), "Айди эпика есть в списке айди его сабтасков");
        assertNotEquals(epicID, subtaskID, "Айди эпика и сабтаска одинаковые");


    }

}