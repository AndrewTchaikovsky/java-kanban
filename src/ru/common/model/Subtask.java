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
        String start = "null";
        if (startTime != null) {
            start = startTime.toString();
        }

        String durationString = "0";
        if (duration != null) {
            durationString = String.valueOf(duration.toMinutes());
        }
        return getId() + "," + getType() + "," + getName() + "," + getStatus() + "," + getDescription() + "," + start + "," + durationString + "," + getEpicID();
    }

    public int getEpicID() {
        return epicID;
    }

}
