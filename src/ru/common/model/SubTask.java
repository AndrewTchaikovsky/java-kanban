package ru.common.model;

public class SubTask extends Task {
    int epicID;

    public SubTask(String name, String description, Status status, int epicID) {
        super(name, description, status);
        this.epicID = epicID;
    }

    public SubTask(int id, String name, String description, Status status, int epicID) {
        super(id, name, description, status);
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return "ru.common.model.SubTask{" +
                "epicID=" + epicID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public int getEpicID() {
        return epicID;
    }
}
