package ru.common.manager;

import ru.common.model.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();

            for (Task task : tasks.values()) {
                writer.write(task.toString());
                writer.newLine();
            }

            for (Subtask subtask : subtasks.values()) {
                writer.write(subtask.toString());
                writer.newLine();
            }

            for (Epic epic : epics.values()) {
                writer.write(epic.toString());
                writer.newLine();
            }

            writer.newLine();

            writer.write(historyManager.toString());
            writer.newLine();

        } catch (IOException e) {
            throw new ManagerSaveException("Can't save to file " + file.getName(), e);
        }

    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            String csv = Files.readString(file.toPath());
            String[] lines = csv.split(System.lineSeparator());

            int maxId = 0;
            List<Integer> history = new ArrayList<>();

            for (int i = 1; i < lines.length; i++) {
                if (lines[i].isEmpty()) {
                    String[] iDs = lines[i + 1].split(",");
                    for (int j = 0; j < iDs.length; j++) {
                        int iD = Integer.parseInt(iDs[j]);
                        history.add(iD);
                    }
                    break;
                }

                Task task = manager.fromString(lines[i]);
                int id = task.getId();
                if (id > maxId) {
                    maxId = id;
                }

                switch (task.getType()) {
                    case TASK:
                        manager.tasks.put(task.getId(), task);
                        break;
                    case EPIC:
                        manager.epics.put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        manager.subtasks.put(task.getId(), (Subtask) task);
                        break;
                }

            }

            for (Subtask subtask : manager.subtasks.values()) {
                Epic epic = manager.epics.get(subtask.getEpicID());
                epic.getSubtaskIDs().add(subtask.getId());
            }

            for (int taskId : history) {
                manager.getHistoryManager().add(manager.getTask(taskId));
            }

            manager.id = maxId;

        } catch (IOException e) {
            throw new ManagerSaveException("Can't load file " + file.getName(), e);
        }

        return manager;
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

    public Task fromString(String value) {
        String[] split = value.split(",");

        int id = Integer.parseInt(split[0]);
        TaskType type = TaskType.valueOf(split[1]);
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];

        Integer epicID = null;
        if (split.length > 5 && split[5] != null) {
            epicID = Integer.parseInt(split[5]);
        }

        switch (type) {
            case TASK:
                return new Task(id, name, description, status);
            case SUBTASK:
                return new Subtask(id, name, description, status, epicID);
            case EPIC:
                return new Epic(id, name, description);
            default:
                throw new IllegalArgumentException("Unknown task type: " + type);
        }
    }

    public File getFile() {
        return file;
    }
}
