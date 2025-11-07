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

    // TASKS

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
        prioritizedTasks.clear();

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
        if (task.getStartTime() == null) {
            return;
        }
        if (hasOverlap(task)) {
            throw new TaskValidationException("Задача пересекается с другой задачей по времени выполнения.");
        }
        prioritizedTasks.add(task);
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
            originalTask.setStartTime(task.getStartTime());
            originalTask.setDuration(task.getDuration());

            delete(originalTask);
            add(originalTask);
        } else {
            System.out.println("Такой задачи не существует.");
        }
    }

    @Override
    public void deleteTask(Integer id) {
        Task task = tasks.remove(id);
        if (task != null) {
            delete(task);
            historyManager.remove(id);
        }
    }

    // SUBTASKS

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Integer> getSubtaskIDs() {
        return epics.values().stream()
                .flatMap(epic -> epic.getSubtaskIDs().stream())
                .toList();
    }

    @Override
    public void deleteEpicSubtasks(Epic epic) {
        new ArrayList<>(epic.getSubtaskIDs()).forEach(id -> {
            if (subtasks.containsKey(id)) {
                deleteSubtask(id);
                historyManager.remove(id);
            }

        } );

        epic.getSubtaskIDs().clear();
        Status epicStatus = calculateEpicStatus(epic);
        epic.setStatus(epicStatus);
    }

    @Override
    public void deleteSubtasks() {
        new ArrayList<>(subtasks.values()).forEach(subtask -> {
            historyManager.remove(subtask.getId());
            delete(subtask);
                });

        subtasks.clear();

        epics.values().forEach(epic -> {
            epic.getSubtaskIDs().clear();
            epic.setStatus(calculateEpicStatus(epic));
            epic.recalculateTimeData(Collections.emptyList());
        });
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
        add(subtask);
        Epic epic = getEpicInternal(subtask.getEpicID());
        epic.getSubtaskIDs().add(subtask.getId());
        Status epicStatus = calculateEpicStatus(getEpicInternal(subtask.getEpicID()));
        getEpicInternal(subtask.getEpicID()).setStatus(epicStatus);

        List<Subtask> epicSubtasks = getEpicSubtasks(epic.getId());
        epic.recalculateTimeData(epicSubtasks);

        return subtask.getId();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Subtask originalSubtask = subtasks.get(subtask.getId());
            originalSubtask.setName(subtask.getName());
            originalSubtask.setDescription(subtask.getDescription());
            originalSubtask.setStatus(subtask.getStatus());
            delete(originalSubtask);
            add(originalSubtask);


            Status epicStatus = calculateEpicStatus(getEpic(subtask.getEpicID()));
            Epic epic = getEpic(subtask.getEpicID());
            epic.setStatus(epicStatus);

            List<Subtask> epicSubtasks = getEpicSubtasks(epic.getId());
            epic.recalculateTimeData(epicSubtasks);

        } else {
            System.out.println("Такой подзадачи не существует.");
        }
    }

    @Override
    public void deleteSubtask(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            delete(subtask);
            subtasks.remove(id);
            historyManager.remove(id);
            Epic relatedEpic = getEpicInternal(subtask.getEpicID());
            relatedEpic.getSubtaskIDs().remove(id);
            Status epicStatus = calculateEpicStatus(relatedEpic);
            Epic epic = getEpicInternal(subtask.getEpicID());
            epic.setStatus(epicStatus);

            List<Subtask> epicSubtasks = getEpicSubtasks(epic.getId());
            epic.recalculateTimeData(epicSubtasks);
        }
    }

    // EPICS

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteEpics() {
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.clear();

        epics.keySet().forEach(historyManager::remove);
        epics.clear();

        prioritizedTasks.clear();
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
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }
        new ArrayList<>(epic.getSubtaskIDs()).forEach(this::deleteSubtask);
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<Integer> getEpicSubtaskIDs(Integer id) {
        return getEpicInternal(id).getSubtaskIDs();
    }

    @Override
    public List<Subtask> getEpicSubtasks(Integer id) {
        return getEpicSubtaskIDs(id).stream()
                .map(this::getSubtask)
                .filter(Objects::nonNull)
                .toList();
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

    // VALIDATION

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    private boolean overlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null || task1.getEndTime() == null || task2.getEndTime() == null) {
            return false;
        }

        return !(task1.getEndTime().isBefore(task2.getStartTime()) || task1.getStartTime().isAfter(task2.getEndTime()));
    }

    public boolean hasOverlap(Task newTask) {
        return prioritizedTasks.stream()
                .filter(existingTask -> existingTask.getId() != newTask.getId())
                .anyMatch(existingTask -> overlap(existingTask, newTask));
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

}
