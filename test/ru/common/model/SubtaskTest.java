package ru.common.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.common.manager.HistoryManager;
import ru.common.manager.InMemoryTaskManager;
import ru.common.manager.Managers;
import ru.common.manager.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    private static TaskManager manager;
    private static HistoryManager historyManager;

    @BeforeAll
    static void setUp() {
        manager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void subtaskObjectsAreEqualIftheirIDsAreEqual() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epic1.getId());
        int subtask1ID = manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Сабтаск 2", "Сабтаск эпика 2", Status.IN_PROGRESS, epic1.getId());
        InMemoryTaskManager.id = subtask1ID;
        manager.createSubtask(subtask2);
        System.out.println(subtask1);
        System.out.println(subtask2);
        assertEquals(subtask1, subtask2, "Объекты Subtask не равны друг другу если равен их ID");
    }

    @Test
    void shouldNotAllowSubtaskToBeItsOwnEpic() {
        InMemoryTaskManager.id = 100;
        Subtask subtask1 = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, 100);
        int subtaskID = manager.createSubtask(subtask1);
        assertEquals(0, subtaskID, "Подзадачу нельзя сделать своим же эпиком");
    }

}