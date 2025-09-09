package ru.common.model;

public class Subtask extends Task {
    int epicID;

    public Subtask(String name, String description, Status status, int epicID) {
        super(name, description, status);
        this.epicID = epicID;
        this.type = TaskType.SUBTASK;
    }

    public Subtask(int id, String name, String description, Status status, int epicID) {
        super(id, name, description, status);
        this.epicID = epicID;
        this.type = TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return getId() + "," + getType() + "," + getName() + "," + getStatus() + "," + getDescription() + "," + getEpicID();
    }

    public int getEpicID() {
        return epicID;
    }

}
