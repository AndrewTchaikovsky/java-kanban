import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, SubTask> epicSubTasks;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        epicSubTasks = new HashMap<>();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + epicSubTasks +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public Status calculateStatus() {
        if (epicSubTasks.isEmpty()) {
            return Status.NEW;
        }
        boolean allNew = true;
        boolean allDone = true;

        for (SubTask subTask : epicSubTasks.values()) {
            Status status = subTask.getStatus();

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

    public HashMap<Integer, SubTask> getSubTasks() {
        return epicSubTasks;
    }

    public void addSubTask(SubTask subTask) {
        epicSubTasks.put(subTask.getId(), subTask);
        subTask.getEpic().setStatus(calculateStatus());
    }
}
