import java.util.HashMap;

public class Epic extends Task {
    HashMap<Integer, SubTask> subTasks;

    public Epic(String name, String description, HashMap<Integer, SubTask> subTasks) {
        super(name, description, calculateStatus(subTasks));
        this.subTasks = subTasks;
    }

    private static Status calculateStatus(HashMap<Integer,SubTask> subTasks) {
        if (subTasks.isEmpty()) {
            return Status.NEW;
        }
        boolean allNew = false;
        boolean allDone = false;

        for (SubTask subTask : subTasks.values()) {
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

}
