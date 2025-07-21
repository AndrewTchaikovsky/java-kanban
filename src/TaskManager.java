import java.util.HashMap;

public class TaskManager {
    public static int id;
    public HashMap<Integer, Task> tasks;
    public HashMap<Integer, SubTask> subTasks;
    public HashMap<Integer, Epic> epics;

    public HashMap<Integer, Task> getAllTasks() {
        return tasks.values();
    }


}
