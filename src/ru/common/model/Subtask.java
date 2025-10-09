package ru.common.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    int epicID;

    public Subtask(String name, String description, Status status, int epicID, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.epicID = epicID;
        this.type = TaskType.SUBTASK;
    }

    public Subtask(int id, String name, String description, Status status, int epicID, LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
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
