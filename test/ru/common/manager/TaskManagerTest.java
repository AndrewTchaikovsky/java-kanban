package ru.common.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.common.model.Epic;
import ru.common.model.Status;
import ru.common.model.Subtask;
import ru.common.model.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    protected abstract T createManager() throws IOException;

    @BeforeEach
    public void setUp() throws IOException {
        manager = createManager();
        task = new Task("Таск 1", "Описание таска 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(60));
        epic = new Epic("Эпик 1", "Описание эпика 1");
        int epicID = manager.createEpic(epic);
        subtask = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epicID, LocalDateTime.now(), Duration.ofMinutes(60));
    }

    @Test
    public void shouldAddAndGetTask() {
        int taskID = manager.createTask(task);
        Task savedTask = manager.getTask(taskID);
        Assertions.assertNotNull(savedTask);
        Assertions.assertEquals(task.getName(), savedTask.getName());
    }

    @Test
    public void shouldLinkSubtaskToEpic() {
        int epicID = manager.createEpic(epic);
        manager.createSubtask(subtask);
        List<Subtask> subtasks = manager.getEpicSubtasks(epicID);

        Assertions.assertTrue(subtasks.contains(subtask), "Подзадача должна принадлежать эпику.");
    }

    @Test
    public void shouldCalculateEpicStatusBasedOnSubtasks() {
        int epicID = manager.createEpic(epic);
        manager.createSubtask(subtask);

        subtask.setStatus(Status.DONE);
        manager.updateSubtask(subtask);

        Assertions.assertEquals(Status.DONE, manager.getEpic(epicID).getStatus());
    }

    @Test
    public void shouldDetectTimeOverlap() {
        manager.createTask(task);
        Task overlapping = new Task("Пересекающаяся задача", "Описание пересекающейся задачи", Status.NEW, task.getStartTime().plusMinutes(30), Duration.ofMinutes(60));

        Assertions.assertThrows(TaskValidationException.class, () -> manager.createTask(overlapping), "Пересечение интервалов должно вызывать TaskValidationException.");
    }


}
