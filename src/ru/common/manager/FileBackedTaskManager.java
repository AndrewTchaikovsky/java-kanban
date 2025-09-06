package ru.common.manager;

import ru.common.model.*;

import java.nio.file.Path;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager  {
    Path path;

    public FileBackedTaskManager(Path path) {
        super();
        this.path = path;
    }

    private void save() {

    }

    @Override
    public List<Task> getTasks() {
        List<Task> list = super.getTasks();
        save();
        return list;
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public Task getTask(Integer id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public int createTask(Task task) {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTask(Integer id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> list = super.getSubtasks();
        save();
        return list;
    }

    @Override
    public List<Integer> getSubtaskIDs() {
        List<Integer> list = super.getSubtaskIDs();
        save();
        return list;
    }

    @Override
    public void deleteEpicSubtasks(Epic epic) {
        super.deleteEpicSubtasks(epic);
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public Subtask getSubtask(Integer id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        int id = super.createSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtask(Integer id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> list = super.getEpics();
        save();
        return list;
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public Epic getEpic(Integer id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public int createEpic(Epic epic) {
        int id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpic(Integer id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public List<Integer> getEpicSubtaskIDs(Integer id) {
        List<Integer> list = super.getEpicSubtaskIDs(id);
        save();
        return list;
    }

    @Override
    public List<Subtask> getEpicSubtasks(Integer id) {
        List<Subtask> list = super.getEpicSubtasks(id);
        save();
        return list;
    }

    @Override
    public Status calculateEpicStatus(Epic epic) {
        Status status = super.calculateEpicStatus(epic);
        save();
        return status;
    }

    @Override
    public HistoryManager getHistoryManager() {
        HistoryManager historyManager = super.getHistoryManager();
        save();
        return historyManager;
    }

    Task fromString(String value) {
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        TaskType type = (split[1]);
        String name = split[2];
         String description;

         Status status;
        return new Task(id, split[2], split[4], split[3]);

    }

}
