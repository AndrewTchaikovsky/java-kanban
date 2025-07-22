import java.util.Collection;
import java.util.HashMap;

public class TaskManager {
    public static int id = 1;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
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

    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void deleteTaskById(Integer id) {
        tasks.remove(id);
    }

    // Subtask-related methods

    public HashMap<Integer, SubTask> getAllSubTasks() {
        HashMap<Integer, SubTask> allSubTasks = new HashMap<>();
        for (Epic epic : epics.values()) {
            HashMap<Integer, SubTask> subTasks = epic.getSubTasks();
            allSubTasks.putAll(subTasks);
        }
        return allSubTasks;
    }

    public void deleteSubTasks() {
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
        }
    }

    public SubTask getSubTaskById(Integer id) {
        HashMap<Integer, SubTask> allSubTasks = new HashMap<>();
        for (Epic epic : epics.values()) {
            HashMap<Integer, SubTask> subTasks = epic.getSubTasks();
            allSubTasks.putAll(subTasks);
        }
        return allSubTasks.get(id);
    }

    public void createSubTask(SubTask subTask) {
        subTask.getEpic().addSubTask(subTask);
    }

    public void updateSubTask(SubTask subTask) {
        subTask.getEpic().addSubTask(subTask);
    }

    public void deleteSubTaskById(Integer id) {
        for (Epic epic : epics.values()) {
            if (epic.getSubTasks().containsKey(id)) {
                epic.getSubTasks().remove(id);
            }
        }
    }

    // Epic-related methods

    public Collection<Epic> getAllEpics() {
        return epics.values();
    }

    public void deleteEpics() {
        epics.clear();
    }

    public Epic getEpicById(Integer id) {
        return epics.get(id);
    }

    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void deleteEpicById(Integer id) {
        epics.remove(id);
    }

    public Collection<SubTask> getEpicSubtasks(Epic epic) {
        return epic.getSubTasks().values();
    }


}
