package ru.common.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.common.manager.HistoryManager;
import ru.common.manager.InMemoryTaskManager;
import ru.common.manager.Managers;
import ru.common.manager.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private static TaskManager manager;
    private static HistoryManager historyManager;

    @BeforeAll
    static void setUp() {
        manager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
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
        Subtask subtask1 = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID);
        int subtaskID = manager.createSubtask(subtask1);

        assertEquals(0, subtaskID, "Эпик нельзя добавить в самого себя в виде подзадачи");
    }

}