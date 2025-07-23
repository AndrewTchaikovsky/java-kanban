package ru.common.manager;

import ru.common.model.Epic;
import ru.common.model.Status;
import ru.common.model.SubTask;
import ru.common.model.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    public static int id = 1;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subtasks;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    // Task-related methods

    public Collection<Task> getAllTasks() {
        return tasks.values();
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public Task getTaskById(Integer id) {
        return tasks.get(id);
    }

    public int createTask(Task task) {
        if (task == null) {
            return 0;
        }
        task.setId(id);
        id++;
        tasks.put(task.getId(), task);
        return task.getId();
    }

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

    public void deleteTaskById(Integer id) {
        tasks.remove(id);
    }

    // Subtask-related methods

    public Collection<SubTask> getAllSubTasks() {
        return subtasks.values();
    }

    public List<Integer> getAllSubTaskIDs() {
        List<Integer> allSubTasks = new ArrayList<>();
        for (Epic epic : epics.values()) {
            List<Integer> subTasks = epic.getSubtaskIDs();
            allSubTasks.addAll(subTasks);
        }
        return allSubTasks;
    }

    public void deleteEpicSubtasks(Epic epic) {
        List<Integer> subtaskIDs = new ArrayList<>(epic.getSubtaskIDs());
        for (Integer subtaskID : subtaskIDs) {
            if (subtasks.containsKey(subtaskID)) {
                deleteSubTaskById(subtaskID);
            }
        }
        epic.getSubtaskIDs().clear();
        Status epicStatus = calculateEpicStatus(epic);
        epic.setStatus(epicStatus);
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            Status epicStatus = calculateEpicStatus(epic);
            epic.setStatus(epicStatus);
        }
    }

    public SubTask getSubTaskById(Integer id) {
        return subtasks.get(id);
    }

    public int createSubTask(SubTask subtask) {
        if (subtask == null) {
            return 0;
        }
        subtask.setId(id);
        id++;
        subtasks.put(subtask.getId(), subtask);
        subtask.getEpic().getSubtaskIDs().add(subtask.getId());
        Status epicStatus = calculateEpicStatus(subtask.getEpic());
        subtask.getEpic().setStatus(epicStatus);
        return subtask.getId();
    }

    public void updateSubTask(SubTask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            SubTask originalSubtask = subtasks.get(subtask.getId());
            originalSubtask.setName(subtask.getName());
            originalSubtask.setDescription(subtask.getDescription());
            originalSubtask.setStatus(subtask.getStatus());
            Status epicStatus = calculateEpicStatus(subtask.getEpic());
            subtask.getEpic().setStatus(epicStatus);

        } else {
            System.out.println("Такой подзадачи не существует.");
        }
    }

    public void deleteSubTaskById(Integer id) {
        SubTask subtask = subtasks.get(id);
        if (subtask != null) {
            subtasks.remove(id);
            Epic relatedEpic = subtask.getEpic();
            relatedEpic.getSubtaskIDs().remove(id);
            Status epicStatus = calculateEpicStatus(relatedEpic);
            subtask.getEpic().setStatus(epicStatus);
        }
    }

    // ru.common.model.Epic-related methods

    public Collection<Epic> getAllEpics() {
        return epics.values();
    }

    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    public Epic getEpicById(Integer id) {
        return epics.get(id);
    }

    public int createEpic(Epic epic) {
        if (epic == null) {
            return 0;
        }
        epic.setId(id);
        id++;
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic originalEpic = epics.get(epic.getId());
            originalEpic.setName(epic.getName());
            originalEpic.setDescription(epic.getDescription());
        } else {
            System.out.println("Такого эпика не существует.");
        }
    }

    public void deleteEpicById(Integer id) {
        List<Integer> subtaskIDs = epics.get(id).getSubtaskIDs();
        for (Integer subtaskID : subtaskIDs) {
            deleteSubTaskById(subtaskID);
        }
        epics.remove(id);
    }

    public List<Integer> getEpicSubtaskIDs(Epic epic) {
        return epic.getSubtaskIDs();
    }

    public Status calculateEpicStatus(Epic epic) {
        List<Integer> subtaskIDs = epic.getSubtaskIDs();
        HashMap<Integer, SubTask> epicSubtasks = new HashMap<>();
        for (SubTask subtask : subtasks.values()) {
            if (subtaskIDs.contains(subtask.getId())) {
                epicSubtasks.put(subtask.getId(), subtask);
            }
        }

        if (epicSubtasks.isEmpty()) {
            return Status.NEW;
        }
        boolean allNew = true;
        boolean allDone = true;

        for (SubTask subtask : epicSubtasks.values()) {
            Status status = subtask.getStatus();

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

}
