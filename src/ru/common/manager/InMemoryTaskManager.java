package ru.common.manager;

import ru.common.model.Epic;
import ru.common.model.Status;
import ru.common.model.Subtask;
import ru.common.model.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    public static int id = 1;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private HistoryManager historyManager;


    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }

    // Task-related methods

    @Override
    public Collection<Task> getTasks() {
        return tasks.values();
    }

    @Override
    public void deleteTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public Task getTask(Integer id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public int createTask(Task task) {
        if (task == null) {
            return 0;
        }
        task.setId(id);
        id++;
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task originalTask = tasks.get(task.getId());
            originalTask.setName(task.getName());
            originalTask.setDescription(task.getDescription());
            originalTask.setStatus(task.getStatus());
        } else {
            System.out.println("Такой задачи не существует.");
        }
    }

    @Override
    public void deleteTask(Integer id) {
        tasks.remove(id);
        historyManager.remove(id);

    }

    // Subtask-related methods

    @Override
    public Collection<Subtask> getSubtasks() {
        return subtasks.values();
    }

    @Override
    public List<Integer> getSubtaskIDs() {
        List<Integer> allsubtasks = new ArrayList<>();
        for (Epic epic : epics.values()) {
            List<Integer> subtasks = epic.getSubtaskIDs();
            allsubtasks.addAll(subtasks);
        }
        return allsubtasks;
    }

    @Override
    public void deleteEpicSubtasks(Epic epic) {
        List<Integer> SubtaskIDs = new ArrayList<>(epic.getSubtaskIDs());
        for (Integer SubtaskID : SubtaskIDs) {
            if (subtasks.containsKey(SubtaskID)) {
                deleteSubtask(SubtaskID);
                historyManager.remove(SubtaskID);
            }
        }
        epic.getSubtaskIDs().clear();
        Status epicStatus = calculateEpicStatus(epic);
        epic.setStatus(epicStatus);
    }

    @Override
    public void deleteSubtasks() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIDs().clear();
            Status epicStatus = calculateEpicStatus(epic);
            epic.setStatus(epicStatus);
        }
    }

    @Override
    public Subtask getSubtask(Integer id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public int createSubtask(Subtask Subtask) {
        if (Subtask == null) {
            return 0;
        }
        Subtask.setId(id);
        id++;
        if (Subtask.getId() == Subtask.getEpicID()) {
            System.out.println("У подзадачи и эпика не может быть одинакового айди.");
            return 0;
        }

        subtasks.put(Subtask.getId(), Subtask);
        getEpic(Subtask.getEpicID()).getSubtaskIDs().add(Subtask.getId());
        Status epicStatus = calculateEpicStatus(getEpic(Subtask.getEpicID()));
        getEpic(Subtask.getEpicID()).setStatus(epicStatus);
        return Subtask.getId();

    }

    @Override
    public void updateSubtask(Subtask Subtask) {
        if (subtasks.containsKey(Subtask.getId())) {
            Subtask originalSubtask = subtasks.get(Subtask.getId());
            originalSubtask.setName(Subtask.getName());
            originalSubtask.setDescription(Subtask.getDescription());
            originalSubtask.setStatus(Subtask.getStatus());
            Status epicStatus = calculateEpicStatus(getEpic(Subtask.getEpicID()));
            getEpic(Subtask.getEpicID()).setStatus(epicStatus);

        } else {
            System.out.println("Такой подзадачи не существует.");
        }
    }

    @Override
    public void deleteSubtask(Integer id) {
        Subtask Subtask = subtasks.get(id);
        if (Subtask != null) {
            subtasks.remove(id);
            historyManager.remove(id);
            Epic relatedEpic = getEpic(Subtask.getEpicID());
            relatedEpic.getSubtaskIDs().remove(id);
            Status epicStatus = calculateEpicStatus(relatedEpic);
            getEpic(Subtask.getEpicID()).setStatus(epicStatus);
        }
    }

    // Epic-related methods

    @Override
    public Collection<Epic> getEpics() {
        return epics.values();
    }

    @Override
    public void deleteEpics() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
    }

    @Override
    public Epic getEpic(Integer id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public int createEpic(Epic epic) {
        if (epic == null) {
            return 0;
        }
        epic.setId(id);
        id++;
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic originalEpic = epics.get(epic.getId());
            originalEpic.setName(epic.getName());
            originalEpic.setDescription(epic.getDescription());
        } else {
            System.out.println("Такого эпика не существует.");
        }
    }

    @Override
    public void deleteEpic(Integer id) {
        List<Integer> SubtaskIDs = epics.get(id).getSubtaskIDs();
        for (Integer SubtaskID : SubtaskIDs) {
            deleteSubtask(SubtaskID);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<Integer> getEpicSubtaskIDs(Integer id) {
        return getEpic(id).getSubtaskIDs();
    }

    @Override
    public List<Subtask> getEpicSubtasks(Integer id) {
        List<Integer> SubtaskIDs = getEpicSubtaskIDs(id);
        List<Subtask> epicsubtasks = new ArrayList<>();
        for (Integer SubtaskID : SubtaskIDs) {
            epicsubtasks.add(getSubtask(SubtaskID));
        }
        return epicsubtasks;
    }

    @Override
    public Status calculateEpicStatus(Epic epic) {
        List<Integer> SubtaskIDs = epic.getSubtaskIDs();
        HashMap<Integer, Subtask> epicsubtasks = new HashMap<>();
        for (Subtask Subtask : subtasks.values()) {
            if (SubtaskIDs.contains(Subtask.getId())) {
                epicsubtasks.put(Subtask.getId(), Subtask);
            }
        }

        if (epicsubtasks.isEmpty()) {
            return Status.NEW;
        }
        boolean allNew = true;
        boolean allDone = true;

        for (Subtask Subtask : epicsubtasks.values()) {
            Status status = Subtask.getStatus();

            if (status != Status.NEW) {
                allNew = false;
            }

            if (status != Status.DONE) {
                allDone = false;
            }

            if (status != Status.NEW && status != Status.DONE) {
                return Status.IN_PROGRESS;
            }
        }

        if (allNew) {
            return Status.NEW;
        }

        if (allDone) {
            return Status.DONE;
        }
        return Status.IN_PROGRESS;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

}
