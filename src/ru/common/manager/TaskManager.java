package ru.common.manager;

import ru.common.model.Epic;
import ru.common.model.Status;
import ru.common.model.Subtask;
import ru.common.model.Task;

import java.util.Collection;
import java.util.List;

public interface TaskManager {

    List<Task> getTasks();

    void deleteTasks();

    Task getTask(Integer id);

    int createTask(Task task);

    void updateTask(Task task);

    void deleteTask(Integer id);

    List<Subtask> getSubtasks();

    List<Integer> getSubtaskIDs();

    void deleteEpicSubtasks(Epic epic);

    void deleteSubtasks();

    Subtask getSubtask(Integer id);

    int createSubtask(Subtask Subtask);

    void updateSubtask(Subtask Subtask);

    void deleteSubtask(Integer id);

    List<Epic> getEpics();

    void deleteEpics();

    Epic getEpic(Integer id);

    int createEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpic(Integer id);

    List<Integer> getEpicSubtaskIDs(Integer id);

    List<Subtask> getEpicSubtasks(Integer id);

    Status calculateEpicStatus(Epic epic);

    HistoryManager getHistoryManager();
}
