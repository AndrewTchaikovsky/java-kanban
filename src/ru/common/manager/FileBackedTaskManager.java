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

    public static void main(String[] args) {

        FileBackedTaskManager manager = new FileBackedTaskManager(new File("resources/file.csv"));

        Task task1 = new Task("Таск 1", "Описание таска 1", Status.NEW);
        Task task2 = new Task("Таск 2", "Описание таска 2", Status.NEW);
        int task1ID = manager.createTask(task1);
        int task2ID = manager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Эпик с 3 подзадачами");
        Epic epic2 = new Epic("Эпик 2", "Эпика без подзадач");
        int epic1ID = manager.createEpic(epic1);
        int epic2ID = manager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Сабтаск 2", "Сабтаск эпика 1", Status.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("Сабтаск 3", "Сабтаск эпика 1", Status.NEW, epic1.getId());
        int subtask1ID = manager.createSubtask(subtask1);
        int subtask2ID = manager.createSubtask(subtask2);
        int subtask3ID = manager.createSubtask(subtask3);

        manager.getTask(task1ID);
        manager.getTask(task2ID);
        manager.getEpic(epic1ID);
        manager.getEpic(epic2ID);
        manager.getSubtask(subtask1ID);
        manager.getSubtask(subtask2ID);
        manager.getSubtask(subtask3ID);

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(new File("resources/file.csv"));

        if (manager.getTasks().equals(newManager.getTasks())) {
            System.out.println("Задачи совпадают.");
        } else {
            System.out.println("Задачи не совпадают.");
        }

        if (manager.getSubtasks().equals(newManager.getSubtasks())) {
            System.out.println("Подзадачи совпадают.");
        } else {
            System.out.println("Подзадачи не совпадают.");
        }

        if (manager.getEpics().equals(newManager.getEpics())) {
            System.out.println("Эпики совпадают.");
        } else {
            System.out.println("Эпики не совпадают.");
        }

        if (manager.getHistoryManager().getHistory().equals(newManager.getHistoryManager().getHistory())) {
            System.out.println("История совпадает.");
        } else {
            System.out.println("История не совпадает.");
        }

    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();

            for (Task task : tasks.values()) {
                writer.write(task.toString());
                writer.newLine();
            }

            for (Epic epic : epics.values()) {
                writer.write(epic.toString());
                writer.newLine();
            }

            for (Subtask subtask : subtasks.values()) {
                writer.write(subtask.toString());
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

            for (int id : history) {
                manager.getHistoryManager().add(manager.getTask(id));
                manager.getHistoryManager().add(manager.getSubtask(id));
                manager.getHistoryManager().add(manager.getEpic(id));
            }

            manager.id = maxId + 1;

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
