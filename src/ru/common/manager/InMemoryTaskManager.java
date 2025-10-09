package ru.common.manager;

import ru.common.model.Epic;
import ru.common.model.Status;
import ru.common.model.Subtask;
import ru.common.model.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

public class InMemoryTaskManager implements TaskManager {
    public static int id = 1;
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, Subtask> subtasks;
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    protected HistoryManager historyManager;


    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }

    // Task-related methods

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
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
        Task task = tasks.get(id);
        if (task == null) {
            return null;
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public int createTask(Task task) {
        if (task == null) {
            return 0;
        }
        task.setId(id);
        id++;
        tasks.put(task.getId(), task);
        add(task);
        return task.getId();
    }

    private void add(Task task) {
        prioritizedTasks.stream()
                .filter(existingTask -> overlap(task, existingTask))
                .findFirst()
                .ifPresentOrElse(
                        overlappedTask -> {
                            String message = "Задача пересекается с задачей с id=" + overlappedTask.getId() + " с датой начала в " + overlappedTask.getStartTime() + " и датой окончания в " + overlappedTask.getEndTime();
                            throw new TaskValidationException(message);
                        },
                        () -> prioritizedTasks.add(task)
                );
    }

    private void delete(Task task) {
        prioritizedTasks.remove(task);
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
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
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
        List<Integer> subtaskIDs = new ArrayList<>(epic.getSubtaskIDs());
        for (Integer subtaskID : subtaskIDs) {
            if (subtasks.containsKey(subtaskID)) {
                deleteSubtask(subtaskID);
                historyManager.remove(subtaskID);
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
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return null;
        }
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        if (subtask == null) {
            return 0;
        }
        subtask.setId(id);
        id++;
        if (subtask.getId() == subtask.getEpicID()) {
            System.out.println("У подзадачи и эпика не может быть одинакового айди.");
            return 0;
        }
        subtasks.put(subtask.getId(), subtask);
        getEpicInternal(subtask.getEpicID()).getSubtaskIDs().add(subtask.getId());
        Status epicStatus = calculateEpicStatus(getEpicInternal(subtask.getEpicID()));
        getEpicInternal(subtask.getEpicID()).setStatus(epicStatus);
        return subtask.getId();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Subtask originalSubtask = subtasks.get(subtask.getId());
            originalSubtask.setName(subtask.getName());
            originalSubtask.setDescription(subtask.getDescription());
            originalSubtask.setStatus(subtask.getStatus());
            Status epicStatus = calculateEpicStatus(getEpic(subtask.getEpicID()));
            getEpic(subtask.getEpicID()).setStatus(epicStatus);

        } else {
            System.out.println("Такой подзадачи не существует.");
        }
    }

    @Override
    public void deleteSubtask(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            subtasks.remove(id);
            historyManager.remove(id);
            Epic relatedEpic = getEpicInternal(subtask.getEpicID());
            relatedEpic.getSubtaskIDs().remove(id);
            Status epicStatus = calculateEpicStatus(relatedEpic);
            getEpicInternal(subtask.getEpicID()).setStatus(epicStatus);
        }
    }

    // Epic-related methods

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
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
        Epic epic = epics.get(id);
        if (epic == null) {
            return null;
        }
        historyManager.add(epic);
        return epic;
    }

    private Epic getEpicInternal(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return null;
        }
        return epic;
    }

    @Override
    public int createEpic(Epic epic) {
        if (epic == null) {
            return 0;
        }
        epic.setId(id);
        id++;
        epic.setStatus(epic.getStatus());
        epic.setSubtaskIDs(new ArrayList<>(epic.getSubtaskIDs()));
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
        List<Integer> subtaskIDs = new ArrayList<>(epics.get(id).getSubtaskIDs());
        for (Integer subtaskID : subtaskIDs) {
            deleteSubtask(subtaskID);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<Integer> getEpicSubtaskIDs(Integer id) {
        return getEpicInternal(id).getSubtaskIDs();
    }

    @Override
    public List<Subtask> getEpicSubtasks(Integer id) {
        List<Integer> subtaskIDs = getEpicSubtaskIDs(id);
        List<Subtask> epicsubtasks = new ArrayList<>();
        for (Integer subtaskID : subtaskIDs) {
            epicsubtasks.add(getSubtask(subtaskID));
        }
        return epicsubtasks;
    }

    @Override
    public Status calculateEpicStatus(Epic epic) {
        List<Integer> subtaskIDs = epic.getSubtaskIDs();
        HashMap<Integer, Subtask> epicsubtasks = new HashMap<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtaskIDs.contains(subtask.getId())) {
                epicsubtasks.put(subtask.getId(), subtask);
            }
        }

        if (epicsubtasks.isEmpty()) {
            return Status.NEW;
        }
        boolean allNew = true;
        boolean allDone = true;

        for (Subtask subtask : epicsubtasks.values()) {
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

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    private boolean overlap(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();

        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return !(start1.isAfter(end2) || start2.isAfter(end1));
    }

    public boolean hasOverlappingTasks() {
        List<Task> tasks = prioritizedTasks.stream()
                .toList();

        if (tasks.size() < 2) return false;

        return IntStream.range(0, tasks.size() - 1)
                .anyMatch(i -> {
                    Task currentTask = tasks.get(i);
                    Task nextTask = tasks.get(i + 1);
                    return currentTask.getEndTime().isAfter(nextTask.getStartTime());
                });
    }

}
